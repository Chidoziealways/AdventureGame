package net.adventuregame.scripting.api

import net.adventuregame.game.GameState
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction

object LuaPlayerAPI {
    fun create(): LuaTable {
        val table = LuaTable()
        val player = GameState.getInstance().player

        table.set("hasItem", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                return valueOf(player.hasItemByName(arg.checkjstring()))
            }
        })

        table.set("giveItem", object : OneArgFunction() {
            override fun call(a: LuaValue): LuaValue {
                val itemName = a.checkjstring()
                player.inventory.addItemByName(itemName)
                return NIL
            }
        })

        table.set("hasKilled", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                return valueOf(player.isBossDefeated(arg.checkjstring()))
            }
        })

        return table
    }
}