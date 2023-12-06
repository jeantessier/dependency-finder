plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ant)
    implementation(libs.log4j)
    implementation(libs.oro)

    testImplementation(libs.junit)
    testImplementation(libs.bundles.jmock)
    testImplementation(libs.httpunit)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
