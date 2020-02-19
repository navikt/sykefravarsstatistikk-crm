package no.nav.arbeidsgiver

import org.slf4j.LoggerFactory

fun main() {
    val log = LoggerFactory.getLogger("main")
    log.info("Starting application")
    Bootstrap.start()
}
