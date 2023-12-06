plugins {
    id("java")
}

dependencies {
    implementation(project(":lib"))

    testImplementation(project("jarjardiff"))
    testImplementation(project("metrics"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
