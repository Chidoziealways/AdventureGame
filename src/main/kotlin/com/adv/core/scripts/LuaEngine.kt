package com.adv.core.scripts

import net.adventuregame.cutscene.Cutscene
import net.adventuregame.entity.Entity
import net.adventuregame.equalsPos
import net.adventuregame.game.GameState
import net.adventuregame.scripting.api.LuaEntityAPI
import net.adventuregame.scripting.api.LuaPlayerAPI
import net.adventuregame.scripting.api.LuaQuestAPI
import org.joml.Vector3f
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File

object LuaEngine {
    private val globals = JsePlatform.standardGlobals()

    fun loadAllScripts() {
        injectAPIs()
        // Load built-in (packaged) scripts
        loadBuiltInScripts()

        // Load modder scripts from external /scripts/
        loadExternalScripts(File("scripts"))

        watchAllScripts()
    }

    private fun loadScript(name: String, content: String, from: String = "unknown") {
        try {
            println("📜 Reloading script from $from: $name")
            val chunk = globals.load(content, name)
            chunk.call()
            println("✅ Script loaded: $name")
        } catch (e: Exception) {
            println("❌ Error loading $name from $from: ${e.message}")
        }
    }

    private fun injectAPIs() {
        val gameState = GameState.getInstance()

        globals.set("Quest", LuaQuestAPI.create())
        globals.set("Player", LuaPlayerAPI.create())
        globals.set("Entity", LuaEntityAPI.create())
        globals.set("posEquals", object : TwoArgFunction() {
            override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue {
                val pos1 = CoerceLuaToJava.coerce(arg1, Vector3f::class.java) as? Vector3f
                val pos2 = CoerceLuaToJava.coerce(arg2, Vector3f::class.java) as? Vector3f

                if (pos1 == null || pos2 == null) {
                    error("Invalid arguments: expected two Vector3f objects")
                }

                return valueOf(pos1?.equalsPos(pos2!!)!!)
            }
        })
        globals.set("addEntity", object : OneArgFunction() {
            override fun call(arg: LuaValue?): LuaValue? {
                GameState.addEntity(CoerceLuaToJava.coerce(arg, Entity::class.java) as Entity)
                return NIL
            }
        })
        globals.set("playCutscene", object : TwoArgFunction() {
            override fun call(arg: LuaValue?, arg1: LuaValue): LuaValue? {
                gameState.playCutscene(CoerceLuaToJava.coerce(arg, Cutscene::class.java) as Cutscene)
                return NIL
            }
        })
    }

    private val builtInScripts = mutableMapOf<String, String>() // name -> script content

    private fun loadBuiltInScripts() {
        val stream = javaClass.classLoader.getResourceAsStream("scripts/index.txt")
        if (stream == null) {
            println("⚠️ No index.txt found in resources/scripts/")
            return
        }

        builtInScripts.clear()

        for (name in stream.bufferedReader().readLines()) {
            val scriptStream = javaClass.classLoader.getResourceAsStream("scripts/$name")
            if (scriptStream != null) {
                val script = scriptStream.bufferedReader().readText()
                builtInScripts[name] = script
                loadScript(name, script, "builtin")
            } else {
                println("⚠️ Script listed in index.txt not found: $name")
            }
        }
    }

    fun watchAllScripts() {
        val scriptDir = File("scripts")
        val watchService = scriptDir.toPath().fileSystem.newWatchService()

        if (scriptDir.exists()) {
            scriptDir.toPath().register(
                watchService,
                java.nio.file.StandardWatchEventKinds.ENTRY_CREATE,
                java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY,
                java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
            )
            println("👀 Watching external script directory: ${scriptDir.absolutePath}")
        }

        Thread {
            println("Thread started!")
            while (true) {
                val key = watchService.take()
                for (event in key.pollEvents()) {
                    val kind = event.kind()
                    val filename = event.context() as? java.nio.file.Path ?: continue
                    if (!filename.toString().endsWith(".lua")) continue

                    val fullPath = scriptDir.toPath().resolve(filename)
                    val file = fullPath.toFile()
                    if (kind.name() == "ENTRY_DELETE") {
                        println("❌ Script deleted: $filename — ignoring")
                    } else {
                        println("🔁 Script changed: $filename")
                        try {
                            val content = file.readText()
                            loadScript(filename.toString(), content, "external")
                        } catch (e: Exception) {
                            println("❌ Error reloading changed script: ${e.message}")
                        }
                    }
                }
                key.reset()
            }
        }.apply { isDaemon = true }.start()

        // Also watch and reload built-in scripts via manual key input (fallback)
        Thread {
            println("Thread Started")
            while (true) {
                Thread.sleep(2000)
                for ((name, content) in builtInScripts) {
                    try {
                        val newStream = javaClass.classLoader.getResourceAsStream("scripts/$name")
                        val newContent = newStream?.bufferedReader()?.readText()
                        if (newContent != null && newContent != content) {
                            println("📦 Detected change in built-in script: $name")
                            builtInScripts[name] = newContent
                            loadScript(name, newContent, "builtin")
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }.apply { isDaemon = true }.start()
    }


    private fun loadExternalScripts(dir: File) {
        if (!dir.exists()) return

        dir.walkTopDown()
            .filter { it.isFile && it.extension == "lua" }
            .forEach { file ->
                try {
                    println("💾 Loading external script: ${file.name}")
                    val script = file.readText()
                    val chunk = globals.load(script, file.name)
                    chunk.call()
                } catch (e: Exception) {
                    println("❌ Error in ${file.name}: ${e.message}")
                }
            }
    }
}
