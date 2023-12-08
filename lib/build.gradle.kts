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
    testImplementation(libs.ant)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    manifest {
        attributes(
                "Specification-Vendor" to "Jean Tessier",
                "Specification-Title" to "Dependency Finder",
                "Specification-Version" to archiveVersion,
//                "Specification-Date" to NOW,
                "Implementation-Vendor" to "Jean Tessier",
                "Implementation-Title" to "Dependency Finder",
                "Implementation-Version" to archiveVersion,
//                "Implementation-Date" to NOW,
                "Implementation-URL" to "https://depfind.sourceforge.io/",
                "Copyright-Holder" to "Jean Tessier",
                "Copyright-Date" to "2001-2023",
                "Compiler-Vendor" to System.getProperty("java.vendor"),
                "Compiler-Title" to System.getProperty("java.runtime.name"),
                "Compiler-Version" to System.getProperty("java.version"),
        )
        attributes(mapOf("Java-Bean" to "True"), "com/jeantessier/dependencyfinder/Version.class")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
