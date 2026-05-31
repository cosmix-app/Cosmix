plugins {
    id("com.android.application")
}

android {
    namespace = "com.cosmix.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cosmix.app"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":library"))
    implementation(libs.appcompat)
}

tasks.register<Jar>("makeJar") {
    archiveFileName.set("classes.jar")
    destinationDirectory.set(layout.buildDirectory)
    
    // Include compiled Kotlin classes from the library module
    from("../library/build/classes/kotlin/android/main")
    
    // Create a fat jar by including JAR dependencies
    from({
        project(":library").configurations.getByName("androidCompileClasspath")
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })
    
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(":library:compileAndroidMain", "assembleRelease")
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from("src/main/kotlin", "src/main/java")
}
