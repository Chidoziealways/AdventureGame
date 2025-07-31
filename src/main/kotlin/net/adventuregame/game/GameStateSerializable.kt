package net.adventuregame.game

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.entities.Entity
import net.adventuregame.player.Player
import net.adventuregame.story.StoryManager

data class GameStateSerializable(
    val seed: Int,
    val player: Player?,
    val storyManager: StoryManager?
    //val entities: List<Entity?>
) {
    companion object {

        @JvmField
        val GAME_STATE_CODEC: Codec<GameStateSerializable> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("seed").forGetter(GameStateSerializable::seed),
                Player.CODEC!!.fieldOf("player").forGetter(GameStateSerializable::player),
                StoryManager.CODEC.fieldOf("story").forGetter(GameStateSerializable::storyManager)
                //Entity.CODEC.listOf().fieldOf("entities").forGetter(GameStateSerializable::entities)
            ).apply(instance, ::GameStateSerializable)
        }
    }

}
