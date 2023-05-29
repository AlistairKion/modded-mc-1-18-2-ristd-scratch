plugins {
    id("java")
}

val glBasePackageName = "com.github.alistairkion.pullpackwizforgeversion"
val glMainClassName = "$glBasePackageName.Main"

group = glBasePackageName
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to glMainClassName
        )
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // add all the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
