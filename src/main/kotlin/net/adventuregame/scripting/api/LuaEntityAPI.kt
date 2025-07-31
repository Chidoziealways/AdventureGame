package net.adventuregame.scripting.api

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction

object LuaEntityAPI {
    private val eventMap = mutableMapOf<String, LuaTable>() // Key = entityType, value = { onDeath = fn, ... }

    fun getEventsForType(type: String): LuaTable? = eventMap[type]

    fun create(): LuaTable {
        val table = LuaTable()

        table.set("on", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                if (!arg.istable()) return NIL
                val t = arg.checktable()
                val type = t.get("type").optjstring(null) ?: return NIL

                val eventTable = LuaTable()
                val onDeath = t.get("onDeath")
                val onDamage = t.get("onDamage")
                val onMove = t.get("onMove")

                if (!onDeath.isnil()) eventTable.set("onDeath", onDeath)
                if (!onDamage.isnil()) eventTable.set("onDamage", onDamage)
                if (!onMove.isnil()) eventTable.set("onMove", onMove)

                eventMap[type] = eventTable
                println("📜 Registered events for entity type: $type")

                return LuaValue.TRUE
            }
        })

        return table
    }
}
