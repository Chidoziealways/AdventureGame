package net.adventuregame.story

import net.adventuregame.game.GameState

class StoryEvent(val name: String, val action: (GameState) -> Unit) {
    fun trigger(gameState: GameState) = action(gameState)
}
