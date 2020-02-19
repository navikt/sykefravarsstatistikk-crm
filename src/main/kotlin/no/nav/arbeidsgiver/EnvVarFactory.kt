package no.nav.arbeidsgiver

object EnvVarFactory {

    private var envVar_: EnvVar? = null

    val envVar: EnvVar
        get() {
            if (envVar_ == null) envVar_ = EnvVar()
            return envVar_ ?: throw AssertionError("Environment factory, null for environment variables!")
        }

    fun set(envVar: EnvVar) {
        envVar_ = envVar
    }
}
