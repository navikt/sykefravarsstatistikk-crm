package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import no.nav.arbeidsgiver.model.SykefravarLeadScoring

object Storage {

    fun loadData(file: File): HashMap<String, SykefravarLeadScoring> {
        val data = HashMap<String, SykefravarLeadScoring>()
        val mapper = ObjectMapper().registerKotlinModule()
        if (file.exists()) {
            file.forEachLine {
                val scoring: SykefravarLeadScoring = mapper.readValue(it)
                data[scoring.orgnr] = scoring
            }
        }
        return data
    }
}
