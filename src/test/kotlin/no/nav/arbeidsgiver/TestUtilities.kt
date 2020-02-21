package no.nav.arbeidsgiver

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import io.findify.s3mock.S3Mock
import kotliquery.queryOf

object TestUtilities {

    fun loadTestData() {
        val session = Dvh.getSession()
        session.run(queryOf(getResourceAsText("/init.sql")).asExecute)
        session.run(queryOf(getResourceAsText("/statistikk_sykefravar.sql")).asExecute)
    }

    fun startS3Mock(port: Int) {
        val api = S3Mock.Builder().withPort(port).withInMemoryBackend().build()

        api.start()
    }

    fun startSfMock(port: Int) {
        val wireMockServer = WireMockServer(wireMockConfig().port(port))
        val testToken = """
        {
            "access_token" : "xxx",
            "instance_url" : "http://localhost:$port",
            "id" : "http://test.salesforce.com/id/xxx/xx",
            "token_type" : "Bearer",
            "issued_at" : "1579532482874",
            "signature" : "xxx="
        }
        """
        wireMockServer.stubFor(
            WireMock.post(SalesforceClient.ENDPOINT_TOKEN).willReturn(
                WireMock.aResponse().withBody(testToken)
            )
        )
        wireMockServer.stubFor(
            WireMock.post(SalesforceClient.ENDPOINT_SOBJECTS).willReturn(
                WireMock.aResponse().withBody("""{
                    "some":"data"
                    }""".trimMargin())
            )
        )
        wireMockServer.start()
    }

    private fun getResourceAsText(path: String): String {
        return this.javaClass.getResource(path).readText()
    }
}
