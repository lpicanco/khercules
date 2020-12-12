plugins {
    application
    kotlin("jvm") version "1.4.10"
}

dependencies {
    implementation(project(":core"))
    implementation("info.picocli:picocli-shell-jline3:4.5.2")

    annotationProcessor("info.picocli:picocli-codegen:4.5.2")
}

application {
    mainClassName = "com.khercules.cli.MainKt"
}

tasks.getByName<JavaExec>("run") {
    standardInput = System.`in`
}


tasks.withType<Jar> {
    manifest {
        attributes("Main-Class" to "com.khercules.cli.MainKt")
    }

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
