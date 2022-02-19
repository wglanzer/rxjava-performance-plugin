import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

allprojects {
    group = properties("pluginGroup")
    version = properties("pluginVersion")

    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "jacoco-report-aggregation")

    tasks {
        // Set the JVM compatibility versions
        properties("javaVersion").let {
            withType<JavaCompile> {
                sourceCompatibility = it
                targetCompatibility = it
            }
            withType<KotlinCompile> {
                kotlinOptions.jvmTarget = it
            }
        }

        test {
            useJUnitPlatform()
        }

        withType<JacocoReport> {
            reports {
                xml.required.set(true)
                csv.required.set(false)
                html.required.set(false)
            }
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        // Annotations
        compileOnly("org.jetbrains:annotations:13.0")

        // JUnit 5
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }
}