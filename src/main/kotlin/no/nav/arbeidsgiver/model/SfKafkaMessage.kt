package no.nav.arbeidsgiver.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SfKafkaMessage(

    val attributes: Map<String, String> = mapOf("type" to "KafkaMessage__c"),

    @JsonProperty("CRM_Topic__c")
    val topic: String,

    @JsonProperty("CRM_Key__c")
    val key: String,

    @JsonProperty("CRM_Value__c")
    val value: String

)
