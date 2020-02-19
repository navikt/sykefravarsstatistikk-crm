package no.nav.arbeidsgiver

import io.kotlintest.specs.StringSpec

class UtilsTest : StringSpec() {

    init {
        "skal kalkulere bransjefravær" {
            val t = Utils.today()
            println(t)
        }
    }
}
