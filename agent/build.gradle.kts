plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    // Byte Buddy to modify runtime bytecode
    implementation("net.bytebuddy:byte-buddy:1.12.8")

    // RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.1.3")

    // SNF4j and GSON for Server/Client communication
    implementation("org.snf4j:snf4j-core:1.8.0")
    implementation("com.google.code.gson:gson:2.9.0")

    // Annotations
    compileOnly("org.jetbrains:annotations:13.0")
}

tasks {
    // Regular Jar
    withType(Jar::class) {
        manifest {
            attributes["Manifest-Version"] = "1.0"
            attributes["Premain-Class"] = "com.github.wglanzer.rxjava.performance.agent.PerformanceAgent"
        }
    }

    // Uber-Jar
    shadowJar {
        manifest {
            attributes(Pair("Premain-Class", "com.github.wglanzer.rxjava.performance.agent.PerformanceAgent"))
        }
    }
}