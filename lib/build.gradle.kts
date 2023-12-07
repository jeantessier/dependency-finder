plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.log4j)
    implementation(libs.oro)

    compileOnly(libs.ant)

    testImplementation(libs.junit)
    testImplementation(libs.bundles.jmock)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
