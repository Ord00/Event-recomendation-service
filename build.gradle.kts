plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.springdoc.openapi-gradle-plugin") version "1.8.0" apply false

}

group = "com.recipemaster"
version = "0.0.1-SNAPSHOT"

dependencyManagement {
	imports {
		mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.3")
	}
}

allprojects {
	apply(plugin = "java")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.springdoc.openapi-gradle-plugin")

	group = "com.recipemaster"
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
		implementation("org.springframework.boot:spring-boot-starter-web")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
