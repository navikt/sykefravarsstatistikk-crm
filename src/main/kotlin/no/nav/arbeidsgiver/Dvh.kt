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

    fun extractStatsNaering(): HashMap<String, SykefravarRadNaring> {
        val output = HashMap<String, SykefravarRadNaring>()
        val sql = """
select naering_kode                             as naring,
       count(distinct concat(arstall, kvartal)) as antall_kvartaler,
       sum(taptedv)                             as sum_tapte_dagsverk,
       sum(muligedv)                            as sum_mulige_dagsverk
from dt_p.agg_ia_sykefravar_naring_kode
where concat(arstall, kvartal) >
      ((select max(concat(arstall, kvartal)) from dt_p.agg_ia_sykefravar_naring_kode) - 30)
group by naering_kode
            """.trimMargin()
        getSession().forEach(queryOf(sql)) { row ->
            val stats = SykefravarRadNaring(
                row.string("naring"),
                row.int("antall_kvartaler"),
                row.int("sum_tapte_dagsverk"),
                row.int("sum_mulige_dagsverk")
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
       naering_kode                             as naring,
       count(distinct concat(arstall, kvartal)) as antall_kvartaler,
       sum(taptedv)                             as sum_tapte_dagsverk,
       sum(muligedv)                            as sum_mulige_dagsverk
from dt_p.agg_ia_sykefravar_v
where concat(arstall, kvartal) >
      ((select max(concat(arstall, kvartal)) from dt_p.agg_ia_sykefravar_v) - 30)
group by orgnr, naering_kode
            """.trimMargin()

        getSession().forEach(queryOf(sql)) { row ->
            val sykefravarRadOrg = SykefravarRadOrg(
                row.string("orgnr"),
                row.string("naring"),
                row.int("antall_kvartaler"),
                row.int("sum_tapte_dagsverk"),
                row.int("sum_mulige_dagsverk")
            )
            val sykefravarRadNaring =
                sykefravarNaring[sykefravarRadOrg.naring] ?: SykefravarRadNaring(sykefravarRadOrg.naring)

            if (sykefravarRadOrg.tapteDagsverk > 0 && sykefravarRadNaring.antallKvartaler > 0) {
                val potensielleDagsverk = Calc.getPotensielleDagsverk(sykefravarRadOrg, sykefravarRadNaring)
                if (potensielleDagsverk.potensielleDagsverk > 0) {
                    statsHandler(potensielleDagsverk)
                }
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
