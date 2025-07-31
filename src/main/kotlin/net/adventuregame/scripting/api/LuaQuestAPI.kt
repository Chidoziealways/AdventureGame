package net.adventuregame.scripting.api

import net.adventuregame.game.GameState
import net.adventuregame.story.Quest
import net.adventuregame.story.StoryManager
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import java.util.function.Supplier

object LuaQuestAPI {
    fun create(): LuaTable {
        val table = LuaTable()

        table.set("add", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val storyManager = GameState.storyManager ?: return LuaValue.NIL

                if (!arg.istable()) {
                    println("⚠️ Quest:add() expects a table, idiot.")
                    return LuaValue.NIL
                }

                val table = arg.checktable()
                val id = table.get("id").optjstring(null)
                val description = table.get("description").optjstring("")

                if (id == null) {
                    println("❌ Quest must have an ID.")
                    return LuaValue.NIL
                }

                val conditionFn = table.get("condition")
                val condition = Supplier {conditionFn.call().toboolean()}

                val quest = Quest(id, description, condition) // Default condition, modder should override

                val onCompleteFn = table.get("onComplete")
                if (!onCompleteFn.isnil()) {
                    quest.setOnCompleteRun {
                        onCompleteFn.call()
                    }
                }

                println("Using StoryManager instance: ${storyManager.hashCode()}")
                storyManager.addQuest(id, quest)

                println("🧾 Lua-defined quest added: $id")
                println("🧾 Lua quest added: $id | description = \"$description\"")
                return LuaValue.TRUE
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
