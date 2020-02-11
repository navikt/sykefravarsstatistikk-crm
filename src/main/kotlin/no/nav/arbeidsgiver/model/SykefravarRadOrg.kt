package no.nav.arbeidsgiver.model

data class SykefravarRadOrg(
    val orgnr: String,
    val naring: String,
    val antallKvartaler: Int,
    val sumTapteDagsverk: Double,
    val sumMuligeDagsverk: Double
)
