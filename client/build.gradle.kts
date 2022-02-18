plugins {
    id("java")
}

dependencies {
    // Core
    implementation(project(":core"))

    // SNF4j and GSON for Server/Client communication
    implementation("org.snf4j:snf4j-core:1.8.0")
    implementation("com.google.code.gson:gson:2.9.0")

    // Annotations
    compileOnly("org.jetbrains:annotations:13.0")
}