plugins {
    id("buildlogic.java-library-conventions")
}

dependencies {
    implementation(project(":client"))
    implementation(platform("software.amazon.awssdk:bom:2.42.28"))
    implementation("software.amazon.awssdk:dynamodb")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    testImplementation("org.mockito:mockito-core:5.17.0")
}
