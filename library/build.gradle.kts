import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.dokka.gradle.engine.parameters.KotlinPlatform
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("maven-publish") // Gradle core plugin
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.serialization)
}

val javaTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())

kotlin {
    version = "1.0.1"

    android {
        // If this is the same com.cosmix.app.R stops working
        namespace = "com.cosmix.api"

        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(javaTarget)
        }

        lint {
            targetSdk = libs.versions.targetSdk.get().toInt()
        }
    }

    jvm()

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-Xannotation-default-target=param-property"
        )
    }

    sourceSets {
        all {
            languageSettings {
                optIn("com.cosmix.app.InternalAPI")
                optIn("com.cosmix.app.Prerelease")
            }
        }

        commonMain.dependencies {
            implementation(libs.annotation) // Annotations
            implementation(libs.nicehttp) // HTTP Lib
            implementation(libs.jackson.module.kotlin) // JSON Parser
            implementation(libs.kotlinx.atomicfu)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json) // JSON Parser
            implementation(libs.jsoup) // HTML Parser
            implementation(libs.rhino) // Run JavaScript
            implementation(libs.tmdb.java) // TMDB API v3 Wrapper Made with RetroFit

            // Deprecated; will be removed once extensions have time to migrate from using it
            implementation("me.xdrop:fuzzywuzzy:1.4.0")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        // We will eventually add a new jvmCommonMain source set
        // for things shared between Android and JVM.
        androidMain.dependencies {
            implementation(libs.newpipeextractor)
        }

        jvmMain.dependencies {
            implementation(libs.newpipeextractor)
        }
    }
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        jvmTarget.set(javaTarget)
    }
}

buildkonfig {
    packageName = "com.cosmix.api"
    exposeObjectWithName = "BuildConfig"

    defaultConfigs {
        // Reads local.properties
        val localProperties = gradleLocalProperties(rootDir, project.providers)
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.cosmix.app"
        }
    }
}

dokka {
    moduleName = "Library"
    dokkaSourceSets {
        configureEach {
            analysisPlatform = KotlinPlatform.AndroidJVM
            documentedVisibilities(
                VisibilityModifier.Public,
                VisibilityModifier.Protected
            )

            sourceLink {
                localDirectory = file("..")
                remoteUrl("https://github.com/cosmix-app/cosmix/tree/master")
                remoteLineSuffix = "#L"
            }
        }
    }
}
