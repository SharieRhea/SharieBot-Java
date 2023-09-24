plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.twitch4j:twitch4j:1.17.0")
    implementation(group = "com.github.philippheuer.events4j", name = "events4j-handler-simple", version = "0.9.8")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.3.5")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}