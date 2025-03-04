plugins {
    id("base")
    id("jacoco-report-aggregation")
}

repositories {
    mavenCentral()
}

dependencies {
    jacocoAggregation(project(":lib"))
    jacocoAggregation(project(":integration-tests"))
    jacocoAggregation(project(":fit-tests"))
    jacocoAggregation(project(":webapp"))
}

reporting {
    reports {
        val testCodeCoverageReport by creating(JacocoCoverageReport::class) {
            testSuiteName = "test"
        }
    }
}
