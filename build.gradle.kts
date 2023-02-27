plugins {
    id("java")
    id("io.freefair.lombok") version("6.6-rc1")
}

group = "at.htlstp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.assertj:assertj-core:3.23.1")
    implementation("com.h2database:h2:2.1.214")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}