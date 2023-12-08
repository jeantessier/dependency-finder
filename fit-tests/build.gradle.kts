plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.fitlibrary)

    testImplementation(libs.junit)
    testImplementation(libs.oro)

    testImplementation(project("metrics"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
