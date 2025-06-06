package net.adventuregame.save

import com.chidozie.core.renderEngine.WindowManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import net.adventuregame.game.GameState
import net.adventuregame.game.GameStateSerializable
import java.io.File

object SaveManager {

    val saveFile = File("saves/world.json")

    fun saveGame(gameState: GameState) {
        val serializable = gameState.toSerializable()
        val result = GameStateSerializable.GAME_STATE_CODEC.encodeStart(JsonOps.INSTANCE, serializable)

        result.result().ifPresentOrElse(
            { jsonElement ->
                val gson = GsonBuilder().setPrettyPrinting().create()
                val prettyJson = gson.toJson(jsonElement)

                saveFile.parentFile.mkdirs()
                saveFile.writeText(prettyJson)
                println("✅ Game saved to ${saveFile.absolutePath}")
            },
            {
                println("❌ Failed to encode GameState for saving.")
            }
        )
    }


    fun loadGame(window: WindowManager): GameState? {
        if (!saveFile.exists()) {
            println("⚠️ No save file found.")
            return null
        }

        val jsonText = saveFile.readText()
        val jsonElement: JsonElement = JsonParser.parseString(jsonText)

        val result = GameStateSerializable.GAME_STATE_CODEC.decode(JsonOps.INSTANCE, jsonElement)

        println("Loading File ${saveFile.absolutePath}")

        return result.result()
            .map { pair -> GameState.fromSerializable(pair.first, window) }
            .orElse(null)
    }

}
