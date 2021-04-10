package net.adoptium.model

// Download represents an abstract entity which a client can downlaod
// It's usually a runtime or jdk
// TODO use adoptopenjdk-api-v3-models, name = JvmImpl
data class Download(
        // human-readable
        val name: String,
        // for api
        val codename: String,

        // human-readable
        val version: String,
        // for api
        val versionCode: String,

        val url: String
) {
    companion object {
        // TODO import OpenJDK V3 API, build Download from their Entities
        /*fun fromRuntime(runtime: Runtime): Download {
            return Download(runtime.);
        }*/
    }
}
