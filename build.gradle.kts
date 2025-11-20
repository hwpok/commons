plugins {
    id("java-library")
}

group = "com.github.hwpok"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.projectlombok:lombok:1.18.42")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    api("jakarta.validation:jakarta.validation-api:3.0.2")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}