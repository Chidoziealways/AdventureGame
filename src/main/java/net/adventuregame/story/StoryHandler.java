package net.adventuregame.story;

import net.adventuregame.game.GameState;
import net.adventuregame.save.SaveManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StoryHandler {

    private static final Logger log = LoggerFactory.getLogger(StoryHandler.class);
    private final StoryManager story;
    private GameState gameState;

    public StoryHandler(GameState gameState) {
        // Load from save if you got one
        this.gameState = gameState;
        this.story = GameState.storyManager;
    }

    // Call this from your main game loop
    public void update() {
        checkQuests();
        maybeAdvance();
        handleDialogue();
    }

    private void checkQuests() {
        for (Quest quest : story.getActiveQuests()) {
            if (quest.isCompleted()) {
                System.out.println("[QUEST COMPLETE] " + quest.getDescription());
            }
        }
    }

    private void maybeAdvance() {
        // This is your progression logic, tailor it
        if (story.getCurrentStage() == StoryStage.THE_AWAKENING &&
                isQuestDone("find_lost_sword")) {
            log.info("Found the 'Lost Sword'");
            story.advanceStage();
            SaveManager.INSTANCE.saveGame(gameState);
        } else if (story.getCurrentStage() == StoryStage.THE_LOST_SWORD &&
                isQuestDone("defeat_shin_shogun")) {
            story.advanceStage();
            SaveManager.INSTANCE.saveGame(gameState);
        }
        // Add more as needed
    }

    private boolean isQuestDone(String id) {
        Quest q = story.getQuest(id);
        return q != null && q.isCompleted();
    }

    private void handleDialogue() {
        Dialogue d = story.getCurrentDialogue();
        if (d != null) {
            while (d.hasNext()) {
                System.out.println("[DIALOGUE] " + d.nextLine());
            }
            story.clearDialogue();
        }
    }
}

