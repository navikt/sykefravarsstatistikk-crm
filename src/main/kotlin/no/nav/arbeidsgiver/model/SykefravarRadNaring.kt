package no.nav.arbeidsgiver.model

data class SykefravarRadNaring(
    val naring: String,
    val antallKvartaler: Int,
    val sumTapteDagsverk: Double,
    val sumMuligeDagsverk: Double
)
