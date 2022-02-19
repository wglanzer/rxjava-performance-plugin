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
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        // Annotations
        compileOnly("org.jetbrains:annotations:13.0")
    }
}