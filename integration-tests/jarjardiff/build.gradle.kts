plugins {
    id("java")
}

dependencies {
    implementation(project("old"))
    implementation(project("old-published"))
    implementation(project("new"))
    implementation(project("new-published"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
