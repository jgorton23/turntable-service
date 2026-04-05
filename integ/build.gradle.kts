plugins {
    id("buildlogic.java-common-conventions")
}

dependencies {
    testImplementation(project(":ddb"))
    testImplementation(project(":client"))
    testImplementation(platform("software.amazon.awssdk:bom:2.42.28"))
    testImplementation("software.amazon.awssdk:dynamodb")
    testImplementation("org.testcontainers:testcontainers:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testCompileOnly("org.projectlombok:lombok:1.18.36")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")
}
