package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.arbeidsgiver.model.SykefravarLeadScoring
import org.slf4j.LoggerFactory

object Bootstrap {

    private val log = LoggerFactory.getLogger(javaClass)

    fun start(ev: EnvVar = EnvVarFactory.envVar) {
        try {
            Server.create(ev)
            work()
        } catch (t: Throwable) {
            log.error(t.cause.toString(), t)
        }
    }

    private fun work() {
        val yesterdaysFile = S3Client.loadFromS3()
        val existingData = Storage.loadData(yesterdaysFile)
        val todaysFile = createTempFile()
        val todaysFileWriter = todaysFile.writer()
        val haveChanged = ArrayList<SykefravarLeadScoring>()
        // Finner endrede rader
        log.info("Starter å hente ut data fra dvh, har " + existingData.size + " rader fra tidligere kjøring.")
        Dvh.extractSykefravarStats({ potensielleDagsverk ->
            if (existingData.containsKey(potensielleDagsverk.orgnr)) {
                if (existingData[potensielleDagsverk.orgnr] != potensielleDagsverk) {
                    haveChanged.add(potensielleDagsverk)
                }
            } else {
                haveChanged.add(potensielleDagsverk)
            }
            todaysFileWriter.appendln(ObjectMapper().writeValueAsString(potensielleDagsverk))
            existingData.remove(potensielleDagsverk.orgnr) // fjerner fikset rad
        }, Dvh.extractStatsNaering())
        todaysFileWriter.close()
        /**
         * Finner rader som skal nullsettes
         */
        existingData.forEach { (_, u) ->
            haveChanged.add(SykefravarLeadScoring(u.orgnr))
        }
        log.info("Skal endre: " + haveChanged.size)
        val chunks = haveChanged.chunked(200)
        if (chunks.isNotEmpty()) {
            chunks.forEach {
                SalesforceClient.postSObject(SalesforceClient.createSObjectList(Const.SYKEFRAVAERSTATS_TOPIC, it))
            }
        }
        S3Client.persistToS3(todaysFile)
    }
}
