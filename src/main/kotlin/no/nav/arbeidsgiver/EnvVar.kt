package no.nav.arbeidsgiver

data class EnvVar(
    val dvhPassword: String = System.getenv("DVH_PASSORD")?.toString() ?: "pass",
    val dvhUrl: String = System.getenv("DVH_DATASOURCE_URL")?.toString() ?: "jdbc:h2:mem:hello",
    val dvhUser: String = System.getenv("DVH_BRUKERNAVN")?.toString() ?: "user",
    val httpsProxy: String = System.getenv("HTTPS_PROXY")?.toString() ?: "",
    val port: Int = System.getenv("PORT")?.toInt() ?: 8087,
    val naisClusterName: String = System.getenv("NAIS_CLUSTER_NAME")?.toString() ?: Const.LOCALDEV,
    val s3AccessKey: String = System.getenv("S3_ACCESS_KEY")?.toString() ?: "",
    val s3Region: String = System.getenv("S3_REGION")?.toString() ?: "us-east-1",
    val s3SecretKey: String = System.getenv("S3_SECRET_KEY")?.toString() ?: "",
    val s3Url: String = System.getenv("S3_URL")?.toString() ?: "http://localhost:8001",
    val sfClientId: String = System.getenv("SALESFORCE_CLIENT_ID")?.toString() ?: "",
    val sfClientSecret: String = System.getenv("SALESFORCE_CLIENT_SECRET")?.toString() ?: "",
    val sfPassword: String = System.getenv("SALESFORCE_PASSWORD")?.toString() ?: "",
    val sfUrl: String = System.getenv("SALESFORCE_URL")?.toString() ?: "http://localhost:8002",
    val sfUsername: String = System.getenv("SALESFORCE_USERNAME")?.toString() ?: "",
    val sfUsertoken: String = System.getenv("SALESFORCE_USERTOKEN")?.toString() ?: ""
)
