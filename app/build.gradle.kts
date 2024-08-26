import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    //id("java")
    application
    jacoco
    id("io.freefair.lombok") version "8.6"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("checkstyle")

}
application {
    mainClass.set("hexlet.code.App")
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("io.javalin:javalin:6.2.0")
    implementation("gg.jte:jte:3.1.12")
    implementation("io.javalin:javalin-rendering:6.1.6")
    implementation("io.javalin:javalin-bundle:6.2.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.h2database:h2:2.2.224")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.puppycrawl.tools:checkstyle:10.14.2")
    implementation("com.konghq:unirest-java:4.0.0-RC2")
    compileOnly("com.konghq:unirest-java-core:4.4.4")
    testImplementation("com.konghq:unirest-object-mappers-gson:4.2.9")
    implementation("com.konghq:unirest-objectmapper-jackson:4.2.9")
    implementation ("org.jsoup:jsoup:1.18.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    implementation("org.postgresql:postgresql:42.7.3")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
        dependsOn(tasks.test) // tests are required to run before generating the report
    }
}


tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }

        rule {
            isEnabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}