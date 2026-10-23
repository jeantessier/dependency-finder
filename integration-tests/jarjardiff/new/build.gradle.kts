plugins {
    id("java")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:-dep-ann")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
