plugins {
    id("java")
    id("jvm-test-suite")
    id("jacoco")
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
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    manifest {
        val releaseDate = findProperty("releaseDate") ?: "unspecified"
        attributes(
                "Specification-Vendor" to "Jean Tessier",
                "Specification-Title" to "Dependency Finder",
                "Specification-Version" to version,
                "Specification-Date" to releaseDate,
                "Implementation-Vendor" to "Jean Tessier",
                "Implementation-Title" to "Dependency Finder",
                "Implementation-Version" to version,
                "Implementation-Date" to releaseDate,
                "Implementation-URL" to "https://jeantessier.github.io/dependency-finder/",
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

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                runtimeOnly("org.junit.vintage:junit-vintage-engine")

                // jMock
                implementation(libs.byte.buddy)
                implementation(libs.jmock.junit4)
                implementation(libs.jmock.junit5)
                implementation(libs.jmock.imposters)

                // Ant
                implementation(libs.ant)
            }
        }
    }
}

// Suppress javadoc warnings about missing documentation comments.
tasks {
    javadoc {
        options {
            (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        }
    }
}
