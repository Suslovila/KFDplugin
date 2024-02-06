import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {

    `kotlin-dsl`
    antlr

}
repositories{
    mavenCentral()
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation("org.antlr:antlr4:4.7.1")
    implementation("org.antlr:antlr4-runtime:4.7.1")
}




