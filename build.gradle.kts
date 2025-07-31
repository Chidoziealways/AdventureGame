plugins {
    id("convention")
}

val group_id: String by project

group = group_id
version = libs.versions.adventuregame.get()

repositories {
    maven { url = uri("https://libraries.minecraft.net") }
    flatDir {
        dirs("libs")
    }
    mavenCentral()
    maven { url = uri("https://maven.google.com") }
}

sourceSets {
    named("main") {
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
    implementation(libs.slf4jApi)
    implementation(libs.log4jApi)
    implementation(libs.log4jCore)
    implementation(libs.log4jSl4fj)
    implementation(libs.compress)
    implementation(libs.lang)
    implementation(libs.brigadier)
    implementation(libs.dfu)
    implementation(libs.authlib)

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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0") // for coroutines
    // or for serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("org.luaj:luaj-jse:3.0.1")
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
