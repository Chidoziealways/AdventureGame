package net.adventuregame.story;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.adventuregame.game.GameState;

import java.util.*;

public class StoryManager {

    private StoryStage currentStage = StoryStage.THE_AWAKENING;
    private final Map<String, Quest> quests = new LinkedHashMap<>();
    private Dialogue currentDialogue = null;

    public StoryManager() {
        initQuests();
    }

    private void initQuests() {
        // Define quests per stage, you can expand these with real game logic conditions
        quests.put("find_lost_sword", new Quest(
                "find_lost_sword",
                "Find the legendary katana in the Forbidden Forest.",
                () -> {
                    // Replace this with your real check, e.g., player inventory or event flag
                    return GameState.player.hasItemByName("katana");
                }));

        quests.put("defeat_shin_shogun", new Quest(
                "defeat_shin_shogun",
                "Defeat the corrupted Shin Shogun and end the Shin Virus.",
                () -> {
                    // Replace with real boss defeated flag
                    return GameState.isBossDefeated("shin_shogun");
                }));
    }

    public StoryStage getCurrentStage() {
        return currentStage;
    }

    public void advanceStage() {
        switch (currentStage) {
            case THE_AWAKENING -> currentStage = StoryStage.THE_LOST_SWORD;
            case THE_LOST_SWORD -> currentStage = StoryStage.THE_SHIN_SHOGUN_RETURN;
            case THE_SHIN_SHOGUN_RETURN -> currentStage = StoryStage.THE_FINAL_CONFRONTATION;
            case THE_FINAL_CONFRONTATION -> currentStage = StoryStage.NEW_DAWN;
            case NEW_DAWN -> {
                // Story complete — maybe unlock new content or loop
            }
        }
        triggerStageEvents();
    }

    private void triggerStageEvents() {
        // Hook in your world event spawning, dialog triggers, cutscenes, etc.
        switch (currentStage) {
            case THE_AWAKENING -> {
                currentDialogue = new Dialogue("Village Elder", List.of(
                        "The prophecy awakens, young warrior...",
                        "Darkness stirs once more in our land."
                ));
            }
            case THE_LOST_SWORD -> {
                currentDialogue = new Dialogue("Old Samurai", List.of(
                        "You must find the lost katana deep within the Forbidden Forest.",
                        "Only then can you challenge the Shin Shogun."
                ));
            }
            case THE_SHIN_SHOGUN_RETURN -> {
                currentDialogue = new Dialogue("Messenger", List.of(
                        "The Shin Shogun grows stronger every day.",
                        "Prepare yourself for the coming battle."
                ));
            }
            case THE_FINAL_CONFRONTATION -> {
                currentDialogue = new Dialogue("Your Spirit", List.of(
                        "This is it. Face the darkness and end this curse!"
                ));
            }
            case NEW_DAWN -> {
                currentDialogue = new Dialogue("Village Elder", List.of(
                        "Peace has returned, but the world will always need heroes."
                ));
            }
        }
    }

    public Dialogue getCurrentDialogue() {
        return currentDialogue;
    }

    public void clearDialogue() {
        currentDialogue = null;
    }

    public Collection<Quest> getActiveQuests() {
        // Filter quests by current stage or all uncompleted quests
        // For simplicity, return all uncompleted quests
        List<Quest> active = new ArrayList<>();
        for (Quest q : quests.values()) {
            if (!q.isCompleted()) active.add(q);
        }
        return active;
    }

    // ======= Save / Load with Codec =======

    public static final Codec<StoryManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("currentStage").forGetter(story -> story.currentStage.name()),
                    Codec.unboundedMap(Codec.STRING, QuestCodec.CODEC).fieldOf("quests").forGetter(story -> story.quests),
                    DialogueCodec.CODEC.optionalFieldOf("currentDialogue").forGetter(story -> Optional.ofNullable(story.currentDialogue))
            ).apply(instance, (stageName, questsMap, dialogueOpt) -> {
                StoryManager sm = new StoryManager();
                sm.currentStage = StoryStage.valueOf(stageName);

                sm.quests.clear();
                sm.quests.putAll(questsMap);
                sm.currentDialogue = dialogueOpt.orElse(null);
                return sm;
            })
    );

    public Quest getQuest(String id) {
        return quests.get(id);
    }


    // Helper Codec wrapper classes for Quest and Dialogue

    public static class QuestCodec implements Codec<Quest> {
        public final String id;
        public final String description;
        public final boolean completed;

        public QuestCodec(Quest quest) {
            this.id = quest.getId();
            this.description = quest.getDescription();
            this.completed = quest.isCompleted();
        }

        public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("id").forGetter(Quest::getId),
                        Codec.STRING.fieldOf("description").forGetter(Quest::getDescription),
                        Codec.BOOL.fieldOf("completed").forGetter(Quest::isCompleted)
                ).apply(instance, (id, description, completed) -> {
                    Quest q = new Quest(id, description, () -> false); // dummy condition, to be restored
                    q.setCompleted(completed);
                    return q;
                })
        );


        @Override
        public <T> DataResult<Pair<Quest, T>> decode(DynamicOps<T> dynamicOps, T t) {
            return null;
        }

        @Override
        public <T> DataResult<T> encode(Quest quest, DynamicOps<T> dynamicOps, T t) {
            return null;
        }
    }


    public static class DialogueCodec implements Codec<Dialogue> {
        public final String speaker;
        public final List<String> lines;
        public final int currentLine;

        public DialogueCodec(Dialogue dialogue) {
            this.speaker = dialogue.getSpeaker();
            this.lines = dialogue.lines;
            this.currentLine = dialogue.currentLine;
        }

        // Factory for codec deserialization
        public static final Codec<Dialogue> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("speaker").forGetter(d -> d.getSpeaker()),
                        Codec.STRING.listOf().fieldOf("lines").forGetter(d -> d.lines),
                        Codec.INT.fieldOf("currentLine").forGetter(d -> d.currentLine)
                ).apply(instance, (speaker, lines, currentLine) -> {
                    Dialogue d = new Dialogue(speaker, lines);
                    d.currentLine = currentLine;
                    return d;
                })
        );

        @Override
        public <T> DataResult<Pair<Dialogue, T>> decode(DynamicOps<T> dynamicOps, T t) {
            return null;
        }

        @Override
        public <T> DataResult<T> encode(Dialogue dialogue, DynamicOps<T> dynamicOps, T t) {
            return null;
        }
    }

}

