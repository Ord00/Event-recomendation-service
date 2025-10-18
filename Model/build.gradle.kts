dependencies {
    implementation(project(":Model"))
    implementation("org.hibernate.orm:hibernate-spatial:6.4.4.Final")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
}