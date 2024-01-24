plugins {
    id("war")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))

    testImplementation(libs.httpunit)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
