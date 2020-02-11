package no.nav.arbeidsgiver

import mu.KotlinLogging

fun main() {
    val log = KotlinLogging.logger {}
    log.info { "Starting application" }
    Bootstrap.start()
}
