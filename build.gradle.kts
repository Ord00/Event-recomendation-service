plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
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
	apply(plugin = "java")
	apply(plugin = "io.spring.dependency-management")

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
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
