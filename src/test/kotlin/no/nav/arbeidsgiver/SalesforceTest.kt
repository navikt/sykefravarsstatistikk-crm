package no.nav.arbeidsgiver

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class SalesforceTest : StringSpec({
    "Token should be parseable" {
        val testToken = """
        {
            "access_token" : "xxx",
            "instance_url" : "https://xxx--preprod.my.salesforce.com",
            "id" : "https://test.salesforce.com/id/xxx/xx",
            "token_type" : "Bearer",
            "issued_at" : "1579532482874",
            "signature" : "xxx="
        }
        """
        val token = Salesforce.mapToken(testToken)
        token.accessToken shouldBe "xxx"
        token.instanceUrl.toString() shouldBe "https://xxx--preprod.my.salesforce.com"
        token.issuedAt shouldBe 1579532482874
    }
    "Should create SObject" {
        val sobject = Salesforce.createSObject("token", mapOf("some" to "data"))
        sobject.topic shouldBe "token"
        sobject.value shouldBe "eyJzb21lIjoiZGF0YSJ9"
    }
    "SObject should be serialized correctly" {
        val sobject = Salesforce.createSObject("token", mapOf("some" to "data"))
        val json = Utils.toJson(sobject)
        json shouldContain "CRM_Topic__c"
        json shouldContain "CRM_Key__c"
        json shouldContain "CRM_Value__c"
        json shouldContain "attributes"
    }
})
