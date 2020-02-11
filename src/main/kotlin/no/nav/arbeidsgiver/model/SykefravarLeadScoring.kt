package no.nav.arbeidsgiver.model

data class SykefravarLeadScoring(
    val orgnr: String,
    val potensielleDagsverk: Int,
    var muligeDagsverk: Int,
    val tapteDagsverk: Int
)
