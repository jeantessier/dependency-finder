plugins {
    id("war")
    id("jvm-test-suite")
    id("jacoco")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))

    // Use JUnit Jupiter test framework.
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.httpunit)
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
