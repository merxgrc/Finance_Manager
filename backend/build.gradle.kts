plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.mrxgrc"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    applicationDefaultJvmArgs = listOf(
        "-Dio.ktor.deployment.config=application.yaml"
    )
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("io.ktor:ktor-server-cors:2.3.5")

    // Ktor core dependencies
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")

    // Logging
    implementation("io.ktor:ktor-server-call-logging:2.3.5")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Serialization
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")

    // Database (Exposed ORM + PostgreSQL)
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.postgresql:postgresql:42.7.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Ktor Test Dependencies
    testImplementation("io.ktor:ktor-server-test-host:2.3.7")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    testImplementation("io.ktor:ktor-client-mock:2.3.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.21")
    testImplementation("org.testng:testng:7.1.0")

    // Plaid dependencies
    implementation("com.plaid:plaid-java:11.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.moshi:moshi-adapters:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("net.java.dev.jna:jna-platform:5.9.0")
}

configurations.all {
    resolutionStrategy.force("com.google.code.gson:gson:2.10.1")
}
