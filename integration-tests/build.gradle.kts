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
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                runtimeOnly("org.junit.vintage:junit-vintage-engine")

                // jMock
                implementation(libs.byte.buddy)
                implementation(libs.jmock.junit3)
                implementation(libs.jmock.junit4)
                implementation(libs.jmock.imposters)

                implementation(project("jarjardiff"))
                implementation(project("metrics"))
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
