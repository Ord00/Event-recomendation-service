group = "com.recipemaster"
version = "0.0.1-SNAPSHOT"

plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":Model"))
    implementation(project(":security-lib"))
    implementation(project(":EventService"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.test {
    useJUnitPlatform()
}