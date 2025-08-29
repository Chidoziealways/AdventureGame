package net.adventuregame.story

import net.adventuregame.game.GameState
import net.adventuregame.save.SaveManager.saveGame
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StoryHandler(private val gameState: GameState) {
    private val story: StoryManager?

    init {
        // Load from save if you got one
        this.story = GameState.storyManager
    }

    // Call this from your main game loop
    fun update() {
        checkQuests()
    }

    private fun checkQuests() {
        for (quest in story!!.activeQuests) {
            if (quest.isCompleted()) {
                println("QUEST COMPLETE: ${quest.description}")
                log.info("[QUEST COMPLETE] " + quest.description)
                story.activeQuests.remove(quest)
                GameState.getInstance().player.currentQuest = null
                saveGame(gameState)
            } else {
                log.info("Not Completed")
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(StoryHandler::class.java)
    }
}

