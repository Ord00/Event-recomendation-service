plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.springdoc.openapi-gradle-plugin") version "1.8.0" apply false

}

group = "event.rec.service"
version = "0.0.1-SNAPSHOT"

allprojects {
	apply(plugin = "java")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.springdoc.openapi-gradle-plugin")

	group = "event.rec.service"
	version = "0.0.1-SNAPSHOT"

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	repositories {
		mavenCentral()
	}
}

subprojects {
	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}

	dependencies {
		compileOnly("org.projectlombok:lombok:1.18.30")
		annotationProcessor("org.projectlombok:lombok:1.18.30")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
		testImplementation(platform("org.junit:junit-bom:5.10.0"))
		testImplementation("org.springframework.boot:spring-boot-starter-test") {
			exclude(group = "org.junit.vintage")
			exclude(group = "org.mockito")
		}
		testImplementation("org.junit.jupiter:junit-jupiter")
		testImplementation("org.testcontainers:testcontainers:1.19.8")
		testImplementation("org.testcontainers:junit-jupiter:1.19.8")
		testImplementation("org.testcontainers:postgresql:1.19.8")
		testImplementation("org.mockito:mockito-core:5.4.0")
		testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
