package no.nav.arbeidsgiver

fun main() {
    TestUtilities.loadTestData()
    val mainPort = 8009
    val s3Port = mainPort + 1
    val sfPort = mainPort + 2
    TestUtilities.startS3Mock(s3Port)
    TestUtilities.startSfMock(sfPort)
    val ev: EnvVar = EnvVarFactory.envVar
    val newEv = ev.copy(
        port = mainPort,
        s3Url = "http://localhost:$s3Port",
        sfUrl = "http://localhost:$sfPort"
        )
    EnvVarFactory.set(newEv)
    Bootstrap.start(newEv)
}
