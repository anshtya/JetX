import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    id("com.codingfeline.buildkonfig") version "0.17.1"
}

kotlin {
    androidLibrary {
        namespace = "com.anshtya.jetx.shared"
        compileSdk = 35
        minSdk = 26

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project.dependencies.platform(libs.supabase.bom))

                implementation(libs.androidx.datastore.preferences)
                implementation(libs.androidx.room.ktx)
                implementation(libs.androidx.room.runtime)
                implementation(libs.koin.annotations)
                implementation(libs.koin.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.supabase.postgrest)
                implementation(libs.supabase.realtime)
                implementation(libs.supabase.storage)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(project.dependencies.platform(libs.firebase.bom))

                implementation(libs.androidx.work.runtime.ktx)
                implementation(libs.firebase.messaging)
                implementation(libs.koin.android)
                implementation(libs.okhttp)
            }
        }
    }

    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    ksp {
        arg("KOIN_CONFIG_CHECK", "true")
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
//    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
}

// Trigger Common Metadata Generation from Native tasks
//project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
//    if (name != "kspCommonMainKotlinMetadata") {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}

buildkonfig {
    packageName = "com.anshtya.jetx.shared"

    val debugUrl = gradleLocalProperties(rootDir, providers).getProperty("DEBUG_URL") ?: ""
    val debugKey = gradleLocalProperties(rootDir, providers).getProperty("DEBUG_KEY") ?: ""
    val releaseUrl = gradleLocalProperties(rootDir, providers).getProperty("RELEASE_URL") ?: ""
    val releaseKey = gradleLocalProperties(rootDir, providers).getProperty("RELEASE_KEY") ?: ""
    val debug = gradleLocalProperties(rootDir, providers).getProperty("DEBUG") ?: ""

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "DEBUG_URL", debugUrl)
        buildConfigField(FieldSpec.Type.STRING, "DEBUG_KEY", debugKey)
        buildConfigField(FieldSpec.Type.STRING, "RELEASE_URL", releaseUrl)
        buildConfigField(FieldSpec.Type.STRING, "RELEASE_KEY", releaseKey)
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", debug)
    }
}