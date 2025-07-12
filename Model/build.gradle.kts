plugins {
    id("java")
}

group = "event.rec.service"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.hibernate.orm:hibernate-spatial:6.4.4.Final")
}

tasks.test {
    useJUnitPlatform()
}