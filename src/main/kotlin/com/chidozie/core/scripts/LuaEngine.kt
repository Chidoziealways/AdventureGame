package com.chidozie.core.scripts

import net.adventuregame.scripting.api.LuaEntityAPI
import net.adventuregame.scripting.api.LuaPlayerAPI
import net.adventuregame.scripting.api.LuaQuestAPI
import org.luaj.vm2.LuaTable
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
    }

    private fun injectAPIs() {
        globals.set("Quest", LuaQuestAPI.create())
        globals.set("Player", LuaPlayerAPI.create())
        globals.set("Entity", LuaEntityAPI.create())
    }

    private fun loadBuiltInScripts() {
        val stream = javaClass.classLoader.getResourceAsStream("scripts/index.txt")
        if (stream == null) {
            println("⚠️ No index.txt found in resources/scripts/")
            return
        }

        val builtInScripts = stream.bufferedReader().readLines()

        for (name in builtInScripts) {
            try {
                val scriptStream = javaClass.classLoader.getResourceAsStream("scripts/$name")
                if (scriptStream != null) {
                    println("📦 Loading built-in script: $name")
                    val script = scriptStream.bufferedReader().readText()
                    val chunk = globals.load(script, name)
                    chunk.call()
                } else {
                    println("⚠️ Script listed in index.txt not found: $name")
                }
            } catch (e: Exception) {
                println("❌ Error in $name: ${e.message}")
            }
        }
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
