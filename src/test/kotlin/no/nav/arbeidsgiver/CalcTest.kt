package no.nav.arbeidsgiver

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.nav.arbeidsgiver.model.SykefravarRadNaring
import no.nav.arbeidsgiver.model.SykefravarRadOrg

class CalcTest : StringSpec() {

    init {
        "skal kalkulere bransjefravær" {
            val sykefravarRadOrg = SykefravarRadOrg("123", "123", 12, 2000, 10000)
            val sykefravarRadNaring = SykefravarRadNaring("123", 12, 1000, 10000)
            val potensielle = Calc.getPotensielleDagsverk(sykefravarRadOrg, sykefravarRadNaring)
            potensielle.muligeDagsverk shouldBe 10000
            potensielle.tapteDagsverk shouldBe 2000
            potensielle.orgnr shouldBe "123"
            potensielle.potensielleDagsverk shouldBe 500
        }
        "skal takle bransjer uten data" {
            val sykefravarRadOrg = SykefravarRadOrg("123", "123", 12, 2000, 10000)
            val sykefravarRadNaring = SykefravarRadNaring("123")
            val potensielle = Calc.getPotensielleDagsverk(sykefravarRadOrg, sykefravarRadNaring)
            potensielle.potensielleDagsverk shouldBe 0
        }
    }
}
