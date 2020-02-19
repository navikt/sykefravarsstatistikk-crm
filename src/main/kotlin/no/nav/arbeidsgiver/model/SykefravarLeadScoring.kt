package no.nav.arbeidsgiver.model

data class SykefravarLeadScoring(
    val orgnr: String,
    val potensielleDagsverk: Int = 0,
    var muligeDagsverk: Int = 0,
    val tapteDagsverk: Int = 0
)
