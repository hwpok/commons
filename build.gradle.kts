plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.hwpok"
version = "1.0.0"

repositories {
    mavenCentral()
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
}

// Javadoc编码配置
tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("encoding", "UTF-8")
        addStringOption("docEncoding", "UTF-8")
        addStringOption("charset", "UTF-8")
        addStringOption("Xdoclint:none", "-quiet")

        if (JavaVersion.current().isJava9Compatible) {
            addBooleanOption("html5", true)
        }
    }
}


java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.hwpok"
            artifactId = "commons"
            version = "1.0.0"

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
