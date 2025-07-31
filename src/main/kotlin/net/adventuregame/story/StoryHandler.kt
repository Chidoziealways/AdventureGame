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
        maybeAdvance()
        handleDialogue()
    }

    private fun checkQuests() {
        for (quest in story!!.activeQuests) {
            if (quest!!.isCompleted()) {
                println("[QUEST COMPLETE] " + quest.description)
            }
        }
    }

    private fun maybeAdvance() {
        // This is your progression logic, tailor it
        if (story!!.currentStage == StoryStage.THE_AWAKENING &&
            isQuestDone("find_lost_sword")
        ) {
            log.info("Found the 'Lost Sword'")
            story.advanceStage()
            saveGame(gameState)
        } else if (story.currentStage == StoryStage.THE_LOST_SWORD &&
            isQuestDone("defeat_shin_shogun")
        ) {
            story.advanceStage()
            saveGame(gameState)
        }
        // Add more as needed
    }

    private fun isQuestDone(id: String?): Boolean {
        val q = story!!.getQuest(id)
        return q != null && q.isCompleted()
    }

    private fun handleDialogue() {
        val d = story!!.currentDialogue
        if (d != null) {
            while (d.hasNext()) {
                println("[DIALOGUE] " + d.nextLine())
            }
            story.clearDialogue()
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(StoryHandler::class.java)
    }
}

