plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.oro)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.bundles.jmock)

    testImplementation(project("jarjardiff"))
    testImplementation(project("metrics"))

    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
