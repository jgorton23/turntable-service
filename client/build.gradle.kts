plugins {
    id("buildlogic.java-library-conventions")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

tasks.javadoc {
    options {
        (this as StandardJavadocDocletOptions).apply {
            addBooleanOption("html5", true)
            encoding = "UTF-8"
            addBooleanOption("Xdoclint:missing", true)
            // addBooleanOption("Werror", true)
        }
    }
}

tasks.build {
    dependsOn(tasks.javadoc)
}