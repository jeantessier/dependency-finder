plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.oro)

    testImplementation(libs.bundles.jmock)

    testImplementation(project("jarjardiff"))
    testImplementation(project("metrics"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
