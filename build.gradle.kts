plugins {
    id("convention")
    kotlin("jvm")
}

val group_id: String by project

group = group_id
version = libs.versions.adventuregame.get()

repositories {
    mavenCentral()
    maven { url = uri("https://libraries.minecraft.net") }
    flatDir {
        dirs("libs")
    }
}

sourceSets {
    named("main") {
        java.srcDirs("src/main/java", "src/main/kotlin")
        java.srcDir("src/main/java")
        kotlin.srcDir("src/main/kotlin")
        resources.srcDir("src/main/resources")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(fileTree("libs") { include("*.jar") })

    val lwjglNatives = libs.versions.lwjglNatives.get()

    implementation(platform(libs.lwjgl))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-assimp")
    implementation("org.lwjgl:lwjgl-bgfx")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-nanovg")
    implementation("org.lwjgl:lwjgl-nuklear")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-openvr")
    implementation("org.lwjgl:lwjgl-par")
    implementation("org.lwjgl:lwjgl-stb")

    implementation(libs.joml)
    implementation(libs.jomlPrim)

    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.failureaccess)
    implementation(libs.errorprone)
    implementation(libs.findbugs)
    implementation(libs.listenablefuture)
    implementation(libs.annotations)
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")
    implementation("org.apache.commons:commons-compress:1.26.0")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("com.mojang:brigadier:1.0.18")
    implementation("com.mojang:datafixerupper:6.0.8")
    implementation("com.mojang:authlib:5.0.47")

    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-assimp::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-bgfx::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-nanovg::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-nuklear::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-openal::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-openvr::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-par::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")

    implementation(libs.jopt.simple)
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

val runGame by tasks.registering(JavaExec::class) {
    mainClass.set("net.adventuregame.game.AdventureMain")
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs = listOf("-Xms512m", "-Xmx2024m")
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
