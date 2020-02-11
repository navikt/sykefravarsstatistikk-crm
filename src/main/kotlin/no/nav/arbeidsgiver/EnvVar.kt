package no.nav.arbeidsgiver

data class EnvVar(
    val dvhPassword: String = System.getenv("DVH_PASSORD")?.toString() ?: "pass",
    val dvhUrl: String = System.getenv("DVH_DATASOURCE_URL")?.toString() ?: "jdbc:h2:mem:hello",
    val dvhUser: String = System.getenv("DVH_BRUKERNAVN")?.toString() ?: "user",
    val httpsProxy: String = System.getenv("HTTPS_PROXY")?.toString() ?: "",
    val naisClusterName: String = System.getenv("NAIS_CLUSTER_NAME")?.toString() ?: Const.LOCALDEV,
    val sfClientId: String = System.getenv("SALESFORCE_CLIENT_ID")?.toString() ?: "",
    val sfClientSecret: String = System.getenv("SALESFORCE_CLIENT_SECRET")?.toString() ?: "",
    val sfPassword: String = System.getenv("SALESFORCE_PASSWORD")?.toString() ?: "",
    val sfUrl: String = System.getenv("SALESFORCE_URL")?.toString() ?: "",
    val sfUsername: String = System.getenv("SALESFORCE_USERNAME")?.toString() ?: "",
    val sfUsertoken: String = System.getenv("SALESFORCE_USERTOKEN")?.toString() ?: "",
    val s3Username: String = System.getenv("S3_USERNAME")?.toString() ?: "",
    val s3Password: String = System.getenv("S3_PASSWORD")?.toString() ?: ""
)
