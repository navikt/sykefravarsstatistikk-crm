package no.nav.arbeidsgiver.model

data class SykefravarRadNaring(
    val naring: String,
    val antallKvartaler: Int = 0,
    val tapteDagsverk: Int = 0,
    val muligeDagsverk: Int = 0
)
