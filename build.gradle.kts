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
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.4.7")
    implementation("org.xerial:sqlite-jdbc:3.43.2.0")
    // implementation("com.tagtraum:ffsampledsp:0.9.53")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}