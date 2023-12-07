plugins {
    id("war")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))

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
