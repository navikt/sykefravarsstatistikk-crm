package no.nav.arbeidsgiver

import no.nav.arbeidsgiver.model.SykefravarLeadScoring
import no.nav.arbeidsgiver.model.SykefravarRadNaring
import no.nav.arbeidsgiver.model.SykefravarRadOrg

object Calc {

    private const val NEDJUSTERING = 0.5

    private const val ANTALL_KVARTALER = 12

    fun getPotensielleDagsverk(statsOrg: SykefravarRadOrg, statsNaering: SykefravarRadNaring): SykefravarLeadScoring {
        val naeringFravaer = (statsNaering.tapteDagsverk.toFloat() / statsNaering.muligeDagsverk.toFloat())
        // println("Naeringsfravær: $naeringFravaer")
        val muligeDagsverk = (statsOrg.muligeDagsverk.toFloat() / statsOrg.antallKvartaler) * ANTALL_KVARTALER
        val tapteDagsverk = (statsOrg.tapteDagsverk.toFloat() / statsOrg.antallKvartaler) * ANTALL_KVARTALER
        val targetFravaer = (naeringFravaer * NEDJUSTERING * muligeDagsverk).toInt()
        // println("Targetfravær: $targetFravaer")
        var potensieltFravaer = 0
        if (naeringFravaer > 0) {
            // Deler på tre for å få ut årlig potensielt fravær
            potensieltFravaer = (tapteDagsverk - targetFravaer).toInt() / 3
        }
        return SykefravarLeadScoring(
            statsOrg.orgnr,
            potensieltFravaer,
            muligeDagsverk.toInt(),
            tapteDagsverk.toInt()
        )
    }
}
