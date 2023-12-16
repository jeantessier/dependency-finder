plugins {
    id("java")
}

val compileJava by tasks.existing {}

val onelevelZip by tasks.register<Zip>("onelevel-zip") {
    dependsOn(compileJava)

    archiveFileName = "onelevel.zip"
    destinationDirectory = layout.buildDirectory.dir("archives")

    from(
            tasks.compileJava,
            "src/main/java",
    )
}

val onelevelJar by tasks.register<Jar>("onelevel-jar") {
    dependsOn(compileJava)

    archiveFileName = "onelevel.jar"
    destinationDirectory = layout.buildDirectory.dir("archives")

    from(
            tasks.compileJava,
            "src/main/java",
    )
}

val onelevelMisc by tasks.register<Zip>("onelevel-mis") {
    dependsOn(compileJava)

    archiveFileName = "onelevel.mis"
    destinationDirectory = layout.buildDirectory.dir("archives")

    from(
            tasks.compileJava,
            "src/main/java",
    )
}

val twolevelZip by tasks.register<Zip>("twolevel-zip") {
    dependsOn(onelevelZip)

    archiveFileName = "twolevel.zip"
    destinationDirectory = layout.buildDirectory.dir("archives")

    from(layout.buildDirectory.file("archives/onelevel.zip"))
}

val twolevelJar by tasks.register<Jar>("twolevel-jar") {
    dependsOn(onelevelZip)

    archiveFileName = "twolevel.jar"
    destinationDirectory = layout.buildDirectory.dir("archives")

    from(layout.buildDirectory.file("archives/onelevel.zip"))
}

val twolevelMisc by tasks.register<Zip>("twolevel-mis") {
    dependsOn(onelevelZip)

    archiveFileName = "twolevel.mis"
    destinationDirectory = layout.buildDirectory.dir("archives")

    from(layout.buildDirectory.file("archives/onelevel.zip"))
}

val classes by tasks.existing {
    dependsOn(
            onelevelZip,
            onelevelJar,
            onelevelMisc,
            twolevelZip,
            twolevelJar,
            twolevelMisc,
            )
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
