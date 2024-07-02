plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.log4j.api)
    implementation(libs.oro)

    compileOnly(libs.ant)

    runtimeOnly(libs.log4j.core)
    runtimeOnly(libs.saxon.he)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.bundles.jmock)
    testImplementation(libs.ant)

    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    manifest {
        val version = System.getenv("DEPENDENCY_FINDER_VERSION") ?: "unknown"
        val releaseDate = System.getenv("DEPENDENCY_FINDER_RELEASE_DATE") ?: "unknown"
        attributes(
                "Specification-Vendor" to "Jean Tessier",
                "Specification-Title" to "Dependency Finder",
                "Specification-Version" to version,
                "Specification-Date" to releaseDate,
                "Implementation-Vendor" to "Jean Tessier",
                "Implementation-Title" to "Dependency Finder",
                "Implementation-Version" to version,
                "Implementation-Date" to releaseDate,
                "Implementation-URL" to "https://depfind.sourceforge.io/",
                "Copyright-Holder" to "Jean Tessier",
                "Copyright-Date" to "2001-2024",
                "Compiler-Vendor" to System.getProperty("java.vendor"),
                "Compiler-Title" to System.getProperty("java.runtime.name"),
                "Compiler-Version" to System.getProperty("java.version"),
        )
        attributes(mapOf("Java-Bean" to "True"), "com/jeantessier/dependencyfinder/Version.class")
    }
}

val copyJarsForDependencies by tasks.register<Copy>("copyJarsForDependencies") {
    from(configurations.runtimeClasspath)
    into(projectDir)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
