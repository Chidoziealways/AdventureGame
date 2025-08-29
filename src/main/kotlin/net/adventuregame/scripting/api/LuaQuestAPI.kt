package net.adventuregame.scripting.api

import net.adventuregame.game.GameState
import net.adventuregame.story.Quest
import net.adventuregame.story.StoryManager
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import java.util.function.Supplier

object LuaQuestAPI {
    fun create(): LuaTable {
        val table = LuaTable()

        table.set("add", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val storyManager = GameState.storyManager ?: return NIL

                val quest = try {
                    CoerceLuaToJava.coerce(arg, Quest::class.java) as Quest
                } catch (e: Exception) {
                    println("❌ Argument to Quest:add() must be a Java Quest object.")
                    return NIL
                }

                println("Using StoryManager instance: ${storyManager.hashCode()}")
                storyManager.addQuest(quest.id!!, quest)

                println("🧾 Lua-defined quest added: ${quest.id}")
                println("🧾 Lua quest added: ${quest.id} | description = \"${quest.description}\"")
                return TRUE
            }
        })

        table.set("has", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val storyManager = GameState.storyManager ?: return LuaValue.FALSE
                return LuaValue.valueOf(storyManager.getQuest(arg.checkjstring()) != null)
            }
        })

        table.set("isComplete", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val storyManager = GameState.storyManager ?: return LuaValue.FALSE
                val quest = storyManager.getQuest(arg.checkjstring())
                return LuaValue.valueOf(quest?.isCompleted() ?: false)
            }
        })

        table.set("forceComplete", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val storyManager = GameState.storyManager ?: return LuaValue.NIL
                storyManager.getQuest(arg.checkjstring())?.forceComplete()
                return LuaValue.NIL
            }
        })

        table.set("stage", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val storyManager = GameState.storyManager ?: return LuaValue.NIL
                return LuaValue.valueOf(storyManager.currentStage.name)
            }
        })

        return table
    }
}
