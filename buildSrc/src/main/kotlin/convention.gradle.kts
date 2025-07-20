plugins {
    java
    `maven-publish`
    idea
    eclipse
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://libraries.minecraft.net") }
    flatDir {
        dirs("libs")
    }

}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes( mapOf(
            "Main-Class" to "net.adventuregame.game.AdventureMain",
            "Specification-Title" to project.property("id"),
            "Specification-Vendor" to project.property("authors"),
            "Specification-Version" to 1,
            "Implementation-Title" to project.name,
            "Implementation-Version" to archiveVersion,
            "Implementation-Vendor" to project.property("authors")
        ))
    }
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}