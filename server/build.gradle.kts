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

plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.openapi)
}

group = project.property("basePackageName") as String
version = project.property("version") as String

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.valueOf("JVM_${libs.versions.java.get()}")
    }
}

dependencies {

//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.79")

    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)

    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.oauth2.resource.server)
    implementation(libs.spring.boot.starter.hateoas)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)

    compileOnly(libs.jakarta.annotation)

    developmentOnly(libs.spring.boot.devtools)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testRuntimeOnly(libs.h2)

}

val openApiOutputDirectory = layout.buildDirectory.dir("generated/openapi")
val openApiSpecification =
    layout.projectDirectory.file("src/main/openapi/openapi.yaml").asFile.absolutePath

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set(openApiSpecification)
    outputDir.set(openApiOutputDirectory.get().asFile.absolutePath)
    apiPackage.set("${project.group}.controller.api")
    modelPackage.set("${project.group}.model.dto")
    invokerPackage.set("${project.group}.openapi")
    modelNameSuffix.set("Dto")
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "useSpringBoot3" to "true",
            "useJakartaEe" to "true",
            "useTags" to "true",
            "dateLibrary" to "java8",
            "documentationProvider" to "none",
            "annotationLibrary" to "none",
            "openApiNullable" to "false",
            "hideGenerationTimestamp" to "true"
        )
    )
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
            "supportingFiles" to "false"
        )
    )
}

openApiValidate {
    inputSpec.set(openApiSpecification)
}

sourceSets {
    main {
        java.srcDir(openApiOutputDirectory.map { it.dir("src/main/java") })
    }
}

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerate"))
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("openApiGenerate"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
