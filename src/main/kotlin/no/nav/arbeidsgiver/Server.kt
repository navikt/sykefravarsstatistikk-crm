package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory

object Server {
    private const val INDEX = "/"
    private const val ISALIVE = "/its-alive"
    private const val ISREADY = "/its-ready"
    private const val S3_FILE = "/s3/get-file"
    private const val SF_ACCOUNT = "/sf/account"
    private const val SF_ACCOUNTS = "/sf/accounts"
    private const val SF_TASKS = "/sf/tasks"
    private const val SF_TOKEN = "/sf/token"
    private val log = LoggerFactory.getLogger(javaClass)
    fun create(envVar: EnvVar): Http4kServer {
        val inLocalDev = envVar.naisClusterName === Const.LOCALDEV
        val inDev = envVar.naisClusterName === Const.DEV_FSS || inLocalDev

        val salesforceTokenHandler: HttpHandler = { _ ->
            val token = SalesforceClient.getCachedToken().copy(accessToken = Const.REDACTED, signature = Const.REDACTED)
            Response(Status.OK).body(Utils.toJson(token).toString())
        }
        val salesforceAccountHandler: HttpHandler = { req: Request ->
            val data = SalesforceClient.getAccountById(req.query("id").toString())
            conditionalResponse(inDev, data)
        }
        val salesforceQueryHandler: HttpHandler = { _ ->
            val data = SalesforceClient.querySalesforce("SELECT name,Id,INT_OrganizationNumber__c from Account")
            conditionalResponse(inDev, data)
        }
        val salesforceQueryTasksHandler: HttpHandler = { _ ->
            val data =
                SalesforceClient.querySalesforce("SELECT AccountId,Id,OwnerId,Description,CreatedDate,Subject from Task")
            conditionalResponse(inDev, data)
        }
        val indexHandler: HttpHandler = { _ ->
            var url = ""
            if (inLocalDev) {
                url = "http://localhost:" + envVar.port
            }
            val data = mapOf(
                "INDEX" to url + INDEX,
                "ISALIVE" to url + ISALIVE,
                "ISREADY" to url + ISREADY,
                "S3_FILE" to url + S3_FILE,
                "SF_ACCOUNT" to url + SF_ACCOUNT,
                "SF_ACCOUNTS" to url + SF_ACCOUNTS,
                "SF_TASKS" to url + SF_TASKS,
                "SF_TOKEN" to url + SF_TOKEN
            )
            val json = ObjectMapper().writeValueAsString(data)
            conditionalResponse(inDev, json)
        }
        val s3FileHandler: HttpHandler = { _ ->
            val file: File = S3Client.loadFromS3()
            conditionalResponse(inLocalDev, file.readText())
        }
        if (envVar.naisClusterName === Const.LOCALDEV) {
            log.info("Starting server http://localhost:" + envVar.port)
        }
        return routes(
            INDEX bind Method.GET to indexHandler,
            ISALIVE bind Method.GET to { Response(Status.OK).body("its alive") },
            ISREADY bind Method.GET to { Response(Status.OK).body("its ready") },
            S3_FILE bind Method.GET to s3FileHandler,
            SF_ACCOUNT bind Method.GET to salesforceAccountHandler,
            SF_ACCOUNTS bind Method.GET to salesforceQueryHandler,
            SF_TASKS bind Method.GET to salesforceQueryTasksHandler,
            SF_TOKEN bind Method.GET to salesforceTokenHandler
        ).asServer(Netty(envVar.port)).start()
    }

    private fun conditionalResponse(inDev: Boolean, data: String): Response {
        return if (inDev) {
            Response(Status.OK).body(data)
        } else {
            Response(Status.OK).body("Debug endepunkt: " + data.length)
        }
    }
}
