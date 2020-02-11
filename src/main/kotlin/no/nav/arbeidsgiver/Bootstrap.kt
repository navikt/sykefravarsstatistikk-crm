package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import mu.KotlinLogging
import no.nav.arbeidsgiver.model.SykefravarLeadScoring

object Bootstrap {

    private val log = KotlinLogging.logger { }

    fun start(ev: EnvVar = EnvVarFactory.envVar) {
        if (ev.naisClusterName === Const.LOCALDEV) {
            Dvh.loadTestData()
        }
        Server.create(ev)
        work(ev.naisClusterName === Const.DEV_FSS)
    }

    private fun work(limitSalesforcePush: Boolean) {
        val existingData = Storage.loadData(Const.SYKEFRAVAERSTATS_FIL)
        val outputStreamWriter = File(Const.SYKEFRAVAERSTATS_FIL).writer()
        val haveChanged = ArrayList<SykefravarLeadScoring>()
        // Finner endrede rader
        Dvh.extractSykefravarStats({ potensielleDagsverk ->
            if (existingData.containsKey(potensielleDagsverk.orgnr)) {
                if (existingData[potensielleDagsverk.orgnr] != potensielleDagsverk) {
                    haveChanged.add(potensielleDagsverk)
                }
            } else {
                haveChanged.add(potensielleDagsverk)
            }
            outputStreamWriter.appendln(ObjectMapper().writeValueAsString(potensielleDagsverk))
            existingData.remove(potensielleDagsverk.orgnr)
        }, Dvh.extractStatsNaering())
        outputStreamWriter.close()
        /**
         * Finner rader som skal nullsettes
         */
        existingData.forEach { (_, u) ->
            haveChanged.add(SykefravarLeadScoring(u.orgnr, 0, 0, 0))
        }
        log.info("Skal endre: " + haveChanged.size)
        val chunks = haveChanged.chunked(200)
        if (chunks.isNotEmpty()) {
            chunks.forEach {
                val requesting = Salesforce.createSObjectList(Const.SYKEFRAVAERSTATS_TOPIC, it)
                Salesforce.postSObject(requesting)
                if (limitSalesforcePush) return@forEach
            }
        }
    }
}
