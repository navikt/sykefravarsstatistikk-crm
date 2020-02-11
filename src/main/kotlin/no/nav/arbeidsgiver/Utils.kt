package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

object Utils {
    fun writeJsonToFile(extract: (Any) -> Unit, file: File) {
        val outputStreamWriter = file.writer()
        extract { stats: JvmType.Object ->
            val json = ObjectMapper().writeValueAsString(stats)
            outputStreamWriter.appendln(json)
        }
        outputStreamWriter.close()
    }

    fun toJson(value: Any): String? {
        val mapper = ObjectMapper().registerKotlinModule()
        return mapper.writeValueAsString(value)
    }

    fun fromJson(string: String): Any {
        val mapper = ObjectMapper().registerKotlinModule()
        return mapper.readValue(string)
    }
}
