plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.github.hwpok"
version = "1.0.6"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    api("jakarta.validation:jakarta.validation-api:3.0.2")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "commons"
            from(components["java"])
        }
    }
}

tasks.named("javadoc") {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}
