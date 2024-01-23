plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.twitch4j:twitch4j:1.19.0")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.4.12")
    implementation("org.xerial:sqlite-jdbc:3.43.2.0")
    implementation("org.openjfx:javafx-plugin:0.1.0")
    implementation("com.mpatric:mp3agic:0.9.1")
    implementation("io.obs-websocket.community:client:2.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}


javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.media")
}

tasks.test {
    useJUnitPlatform()
}