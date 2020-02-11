package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.arbeidsgiver.model.SykefravarLeadScoring
import no.nav.arbeidsgiver.model.SykefravarRadNaring
import no.nav.arbeidsgiver.model.SykefravarRadOrg

object Dvh {

    fun getSession(ev: EnvVar = EnvVarFactory.envVar): Session {
        return sessionOf(ev.dvhUrl, ev.dvhUser, ev.dvhPassword)
    }

    fun loadTestData() {
        val session = getSession()
        session.run(queryOf(File("dvh-testdata/init.sql").readText()).asExecute)
        session.run(queryOf(File("dvh-testdata/statistikk_sykefravar.sql").readText()).asExecute)
    }

    fun extractStatsNaering(): HashMap<String, SykefravarRadNaring> {
        val output = HashMap<String, SykefravarRadNaring>()
        val sql = """
select naring                                   as naring,
       count(distinct concat(arstall, kvartal)) as antall_kvartaler,
       sum(taptedv)                             as sum_tapte_dagsverk,
       sum(muligedv)                            as sum_mulige_dagsverk
from dt_p.v_agg_ia_sykefravar
where concat(arstall, kvartal) >
      ((select max(concat(arstall, kvartal)) from dt_p.v_agg_ia_sykefravar) - 30)
group by naring
            """.trimMargin()
        getSession().forEach(queryOf(sql)) { row ->
            val stats = SykefravarRadNaring(
                row.string("naring"),
                row.int("antall_kvartaler"),
                row.double("sum_tapte_dagsverk"),
                row.double("sum_mulige_dagsverk")
            )
            output[stats.naring] = stats
        }
        return output
    }

    fun extractSykefravarStats(
        statsHandler: (SykefravarLeadScoring) -> Unit,
        sykefravarNaring: HashMap<String, SykefravarRadNaring>
    ) {
        val sql = """
select orgnr                                    as orgnr,
       naring                                   as naring,
       count(distinct concat(arstall, kvartal)) as antall_kvartaler,
       sum(taptedv)                             as sum_tapte_dagsverk,
       sum(muligedv)                            as sum_mulige_dagsverk
from dt_p.v_agg_ia_sykefravar
where concat(arstall, kvartal) >
      ((select max(concat(arstall, kvartal)) from dt_p.v_agg_ia_sykefravar) - 30)
group by orgnr, naring
            """.trimMargin()

        getSession().forEach(queryOf(sql)) { row ->
            val stats = SykefravarRadOrg(
                row.string("orgnr"),
                row.string("naring"),
                row.int("antall_kvartaler"),
                row.double("sum_tapte_dagsverk"),
                row.double("sum_mulige_dagsverk")
            )
            if (stats.sumTapteDagsverk > 0) {
                val statsNaering = sykefravarNaring[stats.orgnr]?.antallKvartaler ?: 0
                val potensielleDagsverk = SykefravarLeadScoring(
                    stats.orgnr,
                    statsNaering,
                    stats.sumMuligeDagsverk.toInt(),
                    stats.sumTapteDagsverk.toInt()
                )
                statsHandler(potensielleDagsverk)
            }
        }
    }

    fun writeStatsToFile(file: File) {
        val outputStreamWriter = file.writer()
        extractSykefravarStats({ stats ->
            val json = ObjectMapper().writeValueAsString(stats)
            outputStreamWriter.appendln(json)
        }, extractStatsNaering())
        outputStreamWriter.close()
    }
}
