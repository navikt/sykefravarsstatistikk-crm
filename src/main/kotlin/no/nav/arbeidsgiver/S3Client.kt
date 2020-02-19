package no.nav.arbeidsgiver

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.CreateBucketRequest
import com.amazonaws.services.s3.model.PutObjectResult
import com.amazonaws.services.s3.transfer.TransferManager
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import java.io.File
import org.slf4j.LoggerFactory

object S3Client {

    private val s3: AmazonS3
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        val ev = EnvVarFactory.envVar
        val credentials = BasicAWSCredentials(ev.s3AccessKey, ev.s3SecretKey)
        log.info("New Client: (host: " + ev.s3Url + " - " + ev.s3Region + ", accesskey-length: " + ev.s3AccessKey.length + "S3 secret key Length: " + ev.s3SecretKey.length)
        s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(ev.s3Url, ev.s3Region))
            .enablePathStyleAccess()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()
        createBucketIfMissing()
    }

    private fun createBucketIfMissing() {
        val bucketList = s3.listBuckets().filter { b -> b.name == Const.SYKEFRAVAERSTATS_BUCKET }
        if (bucketList.isEmpty()) {
            log.info("Creating new bucket as its missing: " + Const.SYKEFRAVAERSTATS_BUCKET)
            s3.createBucket(CreateBucketRequest(Const.SYKEFRAVAERSTATS_BUCKET).withCannedAcl(CannedAccessControlList.Private))
        }
        if (!s3.doesObjectExist(Const.SYKEFRAVAERSTATS_BUCKET, Const.SYKEFRAVAERSTATS_FIL)) {
            log.info("Creating empty file for persisting what have been pushed: " + Const.SYKEFRAVAERSTATS_FIL)
            s3.putObject(Const.SYKEFRAVAERSTATS_BUCKET, Const.SYKEFRAVAERSTATS_FIL, "")
        }
    }

    /**
     * Lagrer en filreferanse til S3
     */
    fun persistToS3(file: File): PutObjectResult {
        return s3.putObject(Const.SYKEFRAVAERSTATS_BUCKET, Const.SYKEFRAVAERSTATS_FIL, file)
    }

    private fun transferManager(): TransferManager {
        return TransferManagerBuilder.standard().withS3Client(s3).build()
    }

    /**
     * Laster object fra S3 og returnerer en filreferanse
     */
    fun loadFromS3(): File {
        val tempFile = createTempFile()
        transferManager()
            .download(Const.SYKEFRAVAERSTATS_BUCKET, Const.SYKEFRAVAERSTATS_FIL, tempFile)
            .waitForCompletion()
        return tempFile
    }
}
