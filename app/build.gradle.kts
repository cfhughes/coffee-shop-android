/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.ksp)
    alias(libs.plugins.openapi)
    alias(libs.plugins.schema.parser)
    alias(libs.plugins.junit)
}

val openApiOutputDirectory = layout.buildDirectory.dir(
    project.property("openApiOutputDirectory") as String
)
val openApiSpecification = layout.projectDirectory.file(
    project.property("openApiSpecification") as String
).asFile.absolutePath

openApiGenerate {
    generatorName.set(project.property("openApiGeneratorName") as String)
    library.set(project.property("openApiLibrary") as String)
    inputSpec.set(openApiSpecification)
    outputDir.set(openApiOutputDirectory.get().asFile.absolutePath)
    apiPackage.set(project.property("openApiApiPackage") as String)
    modelPackage.set(project.property("openApiModelPackage") as String)
    modelNameSuffix.set(project.property("openApiModelNameSuffix") as String)
    configOptions.set(
        mapOf(
            "serializationLibrary" to project.property("openApiSerializationLibrary") as String,
            "useCoroutines" to project.property("openApiUseCoroutines") as String,
            "dateLibrary" to project.property("openApiDateLibrary") as String,
            "documentationProvider" to "none",
            "hideGenerationTimestamp" to "true"
        )
    )
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
            "apiDocs" to "false",
            "apiTests" to "false",
            "modelDocs" to "false",
            "modelTests" to "false",
            "supportingFiles" to "CollectionFormats.kt"
        )
    )
}

openApiValidate {
    inputSpec.set(openApiSpecification)
}

android {

    namespace = project.property("basePackageName") as String
    compileSdk = (project.property("targetSdk") as String).toInt()

    defaultConfig {

        applicationId = project.property("basePackageName") as String
        minSdk = (project.property("minSdk") as String).toInt()
        targetSdk = (project.property("targetSdk") as String).toInt()
        versionCode = (project.property("versionCode") as String).toInt()
        versionName = project.property("version") as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"

        resValue("string", "app_name", project.property("appName") as String)

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.java.get()}")
        targetCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.java.get()}")
    }

    buildFeatures {
        resValues = true
        viewBinding = true
        // Enable dataBinding if desired.
        // dataBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/LICENSE.md"
        }
    }

    sourceSets {
        named("main") {
            kotlin.directories.add(
                openApiOutputDirectory.get().dir("src/main/kotlin").asFile.absolutePath
            )
        }
    }

}

tasks.named("preBuild") {
    dependsOn(tasks.named("openApiGenerate"))
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.valueOf("JVM_${libs.versions.java.get()}")
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

ksp {
    mapOf(
        "room.schemaLocation" to "$projectDir/schemas",
        "room.incremental" to "true",
        "room.expandProjection" to "true"
    ).forEach { (key, value) ->
        arg(key, value)
    }
}

dependencies {

    // .jar-based libraries included in project

    // Kotlin core & async libraries
    implementation(libs.kotlin)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.jdk8)
    implementation(libs.kotlin.coroutines.android)

    // Basic Android components
    implementation(libs.app.compat)
    implementation(libs.activity)
    implementation(libs.fragment)

    // Common additional widgets
    implementation(libs.constraint.layout)
    implementation(libs.recycler.view)

    // Navigation framework libraries
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Lifecycle (LiveData & ViewModel) libraries
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // Preferences/settings components
    implementation(libs.preference)

    // Material Design components & styles
    implementation(libs.material)

    // Room ORM library & annotation processor
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Hilt dependency-injection library & annotation processor
    implementation(libs.hilt.android.core)
    implementation(libs.dagger.core)
    ksp(libs.hilt.compiler)

    // Gson (Google JSON parser) library
    // implementation(libs.gson)

    // Moshi (Square JSON parser) library
    implementation(libs.moshi.core)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    // implementation(libs.moshi.adapters)

    // Android/Google authentication libraries
    // implementation(libs.play.auth) // Legacy
    // implementation(libs.credentials)
    // implementation(libs.credentials.play.services)
    // implementation(libs.googleid)

    // Retrofit (REST client) with Gson & RxJava integration
    implementation(libs.retrofit.core)
    // implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.moshi)
    // implementation(libs.retrofit.adapter.rx.java)
    // implementation(libs.retrofit.adapter.java8)

    // OkHttp core & logging libraries
    // implementation(libs.okio)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Libraries for JVM-based testing.
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testRuntimeOnly(libs.junit.engine)

    // Libraries for instrumented (run in Android) testing.
    androidTestImplementation(libs.android.test.runner)
    androidTestImplementation(libs.junit.android.core)
    androidTestRuntimeOnly(libs.junit.android.runner)
    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.junit.params)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestAnnotationProcessor(libs.hilt.compiler)
    androidTestAnnotationProcessor(libs.hilt.android.compiler)

    constraints {
        implementation(libs.kotlin.jdk7) {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        //noinspection ForeignDelegate
        implementation(libs.kotlin.jdk8) {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }
}

roomDdl {
    // TODO Change source location to match local database.
    source.set(project.file("$projectDir/schemas/edu.cnm.deepdive.myproject.service.LocalDatabase/1.json"))
    destination.set(project.file("$projectDir/../docs/sql/ddl.sql"))
}

fun getLocalProperty(name: String): String {
    return getProperty("$projectDir/local.properties", name)
}

fun getProperty(filename: String, name: String): String {
    return FileInputStream(filename).use {
        val props = Properties()
        props.load(it)
        props.getProperty(name)
    }
}
