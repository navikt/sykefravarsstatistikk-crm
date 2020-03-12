package no.nav.arbeidsgiver

import io.kotlintest.Spec
import io.kotlintest.matchers.string.shouldEndWith
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import java.io.File
import java.util.*
import kotliquery.queryOf
import no.nav.arbeidsgiver.model.SykefravarLeadScoring

class DvhTest : StringSpec() {
    /**
     * Setting up the tables in DVH
     * https://github.com/navikt/sykefravarsstatistikk-api/blob/master/src/main/resources/db/test-datavarehus/V0.01__dvh_init.sql
     */
    override fun beforeSpec(spec: Spec) {
        TestUtilities.loadTestData()
        val session = Dvh.getSession()
        val insertQuery = "insert into dt_p.v_dim_ia_sektor (sektorkode,  sektornavn) values (?, ?)"
        session.run(queryOf(insertQuery, "A", "Alice").asUpdate) // returns effected row count
        session.run(queryOf(insertQuery, "B", "Bob").asUpdate)
    }

    init {

        "Should return Alice" {
            val session = Dvh.getSession()
            session.forEach(queryOf("select * from dt_p.v_dim_ia_sektor LIMIT 1")) { row ->
                row.stringOrNull("sektorkode") shouldBe "A"
            }
        }

        "Should getSyfraStats fra apiet" {
            val potensielleDagsverk = ArrayList<SykefravarLeadScoring>()
            Dvh.extractSykefravarStats({ stats ->
                potensielleDagsverk.add(stats)
            }, Dvh.extractStatsNaering())
            println(potensielleDagsverk)
            potensielleDagsverk[0].orgnr shouldBe "987654321"
            potensielleDagsverk[0].potensielleDagsverk shouldBe 752
            potensielleDagsverk[0].muligeDagsverk shouldBe 12000
            potensielleDagsverk[0].tapteDagsverk shouldBe 3600
        }

        "Should write stats to file" {
            val testFile = File.createTempFile("test", "json")
            Dvh.writeStatsToFile(testFile)
            testFile.forEachLine {
                it shouldStartWith "{"
                it shouldEndWith "}"
            }
        }
        "Should extractStatsNaering" {
            val naringStat = Dvh.extractStatsNaering()["88911"]
            naringStat shouldNotBe null
            if (naringStat != null) {
                naringStat.naring shouldBe "88911"
                naringStat.antallKvartaler shouldBe 3
                naringStat.tapteDagsverk shouldBe 828
                naringStat.muligeDagsverk shouldBe 3700
            }
        }
    }

    private fun getResourceAsText(path: String): String {
        return object {}.javaClass.getResource(path).readText()
    }
}
