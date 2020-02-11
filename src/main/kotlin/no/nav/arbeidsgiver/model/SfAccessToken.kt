package no.nav.arbeidsgiver.model

import java.net.URI

data class SfAccessToken(
    val accessToken: String,
    val instanceUrl: URI,
    val id: URI,
    val tokenType: String,
    val issuedAt: Long,
    val signature: String
)
