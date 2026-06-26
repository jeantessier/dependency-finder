plugins {
    id("java")
    id("jvm-test-suite")
    id("jacoco")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.oro)

    // Use JUnit Jupiter test framework.
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.bundles.jmock)
    testImplementation(project("jarjardiff"))
    testImplementation(project("metrics"))
}

testing {
    suites {
        val test = getByName<JvmTestSuite>("test") {
            useJUnitJupiter()
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
