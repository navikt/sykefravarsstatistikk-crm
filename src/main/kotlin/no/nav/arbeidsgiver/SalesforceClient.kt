package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.net.URI
import java.util.*
import net.logstash.logback.argument.StructuredArguments
import no.nav.arbeidsgiver.model.SfAccessToken
import no.nav.arbeidsgiver.model.SfKafkaMessage
import no.nav.arbeidsgiver.model.SykefravarLeadScoring
import org.apache.http.HttpHost
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClients
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import org.slf4j.LoggerFactory

/**
 * https://github.com/navikt/ereg-sf/blob/master/src/main/kotlin/no/nav/ereg/SalesforceDSL.kt
 */
object SalesforceClient {
    private val log = LoggerFactory.getLogger(javaClass)
    private var tokenTimeout = 3600000; // One hour
    private var token: SfAccessToken? = null
    const val ENDPOINT_SOBJECTS = "/services/data/v47.0/composite/sobjects"
    const val ENDPOINT_TOKEN = "/services/oauth2/token"
    private const val ENDPOINT_QUERY = "/services/data/v20.0/query/"
    private const val ENDPOINT_ACCOUNT = "/services/data/v39.0/sobjects/Account/"
    private fun getToken(ev: EnvVar = EnvVarFactory.envVar): SfAccessToken {
        val endpointUri = ev.sfUrl + ENDPOINT_TOKEN
        log.info("Making request to: $endpointUri")
        val validRequest = Request(Method.POST, endpointUri)
            .form("grant_type", "password")
            .form("client_id", ev.sfClientId)
            .form("client_secret", ev.sfClientSecret)
            .form("username", ev.sfUsername)
            .form("password", ev.sfPassword + ev.sfUsertoken)
            .header("Content-Type", "application/x-www-form-urlencoded")
        val response = getHTTPClient()(validRequest)
        log.info("Received token with status ${response.status}")
        if (response.status !== Status.OK) {
            return mapToken(response.bodyString())
        } else {
            log.error("${response.status} - ${response.bodyString()}")
            throw Exception(response.bodyString())
        }
    }

    fun getCachedToken(ev: EnvVar = EnvVarFactory.envVar): SfAccessToken {
        if (token === null || tokenIsOld(token!!)) {
            token = getToken(ev)
        }
        return token as SfAccessToken
    }

    fun mapToken(tokenJson: String): SfAccessToken {
        val mapper = ObjectMapper().registerKotlinModule()
        mapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        val token: SfAccessToken = mapper.readValue(tokenJson)
        log.info("Token issued at: " + token.issuedAt)
        return token
    }

    private fun tokenIsOld(token: SfAccessToken): Boolean {
        val currentTime: Long = Calendar.getInstance().timeInMillis
        return (currentTime > token.issuedAt + tokenTimeout)
    }

    private fun createAuthorizedRequest(method: Method, endpointPath: String): Request {
        val token = getCachedToken()
        val endpointUri = token.instanceUrl.toString() + endpointPath
        return Request(method, endpointUri)
            .header("Authorization", token.tokenType + " " + token.accessToken)
            .header("Content-Type", "application/json;charset=UTF-8")
    }

    private fun getHTTPClient(httpsProxy: String = EnvVarFactory.envVar.httpsProxy) =
        if (httpsProxy.isNotEmpty())
            ApacheClient(
                client = HttpClients.custom()
                    .setDefaultRequestConfig(
                        RequestConfig.custom()
                            .setProxy(HttpHost(URI(httpsProxy).host, URI(httpsProxy).port, URI(httpsProxy).scheme))
                            .setRedirectsEnabled(false)
                            .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                            .build()
                    )
                    .build()
            ) else ApacheClient()

    fun getAccountById(accountId: String): String {
        val endpointPath = ENDPOINT_ACCOUNT + accountId
        val request = createAuthorizedRequest(Method.GET, endpointPath)
        val result = getHTTPClient()(request)
        requestLogger(endpointPath, result)
        return result.bodyString()
    }

    // curl https://yourInstance.salesforce.com/services/data/v20.0/query/?q=SELECT+name+from+Account -H "Authorization: Bearer token"
    fun querySalesforce(sosql: String): String {
        val request = createAuthorizedRequest(Method.GET, ENDPOINT_QUERY)
            .query("q", sosql)
        val response = getHTTPClient()(request)
        requestLogger(ENDPOINT_QUERY, response)
        return response.bodyString()
    }

    fun postSObject(jsonString: String): String {
        val request = createAuthorizedRequest(Method.POST, ENDPOINT_SOBJECTS)
            .body(jsonString)
        val response = getHTTPClient()(request)
        requestLogger(ENDPOINT_SOBJECTS, response)
        return response.bodyString()
    }

    fun createSObject(topic: String, value: Any, key: String = UUID.randomUUID().toString()): SfKafkaMessage {
        val payload = ObjectMapper().writeValueAsString(value).toByteArray()
        return SfKafkaMessage(
            mapOf("type" to Const.SOBJECT_TYPE),
            topic,
            key,
            Base64.getEncoder().encodeToString(payload)
        )
    }

    // https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_composite_sobjects_collections_create.htm
    fun createSObjectList(topic: String, objects: List<SykefravarLeadScoring>): String {
        val sObjects = objects.map { record ->
            createSObject(topic, record, record.orgnr + ":" + record.hashCode())
        }
        val records = mapOf("records" to sObjects)
        return Utils.toJson(records).toString()
    }

    private fun requestLogger(endpointPath: String, response: Response) {
        if (response.status !== Status.OK) {
            log.info(
                "Status " + response.status + ", length: " + response.bodyString().length,
                StructuredArguments.value("body", response.bodyString()),
                StructuredArguments.value("endpoint", endpointPath)
            )
        } else {
            log.warn(
                "Status " + response.status + ", length: " + response.bodyString().length,
                StructuredArguments.value("body", response.bodyString()),
                StructuredArguments.value("endpoint", endpointPath)
            )
        }
    }
}
