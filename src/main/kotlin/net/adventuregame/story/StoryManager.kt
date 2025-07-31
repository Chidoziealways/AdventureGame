package net.adventuregame.story

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.game.GameState
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class StoryManager {
    var currentStage: StoryStage = StoryStage.THE_AWAKENING
        private set
    private val quests: MutableMap<String?, Quest> = LinkedHashMap<String?, Quest>()
    var currentDialogue: Dialogue? = null
        private set

    fun addQuest(id: String, quest: Quest) {
        quests[id] = quest
        println("Quest Added: $id")
        println("Total Quests: ${quests.keys}")
    }

    private fun initQuests() {
        // Define quests per stage, you can expand these with real game logic conditions
        quests.put(
            "find_lost_sword", Quest(
                "find_lost_sword",
                "Find the legendary katana in the Forbidden Forest."
            ) { GameState.player!!.hasItemByName("katana") }
        )

        quests.put(
            "defeat_shin_shogun", Quest(
                "defeat_shin_shogun",
                "Defeat the corrupted Shin Shogun and end the Shin Virus.",
                Supplier { GameState.player!!.isBossDefeated("shin_shogun") })
        )
    }

    fun advanceStage() {
        when (currentStage) {
            StoryStage.THE_AWAKENING -> currentStage = StoryStage.THE_LOST_SWORD
            StoryStage.THE_LOST_SWORD -> currentStage = StoryStage.THE_SHIN_SHOGUN_RETURN
            StoryStage.THE_SHIN_SHOGUN_RETURN -> currentStage = StoryStage.THE_FINAL_CONFRONTATION
            StoryStage.THE_FINAL_CONFRONTATION -> currentStage = StoryStage.NEW_DAWN
            StoryStage.NEW_DAWN -> {
                // Story complete — maybe unlock new content or loop
            }
        }
        triggerStageEvents()
    }

    private fun triggerStageEvents() {
        // Hook in your world event spawning, dialog triggers, cutscenes, etc.
        when (currentStage) {
            StoryStage.THE_AWAKENING -> {
                currentDialogue = Dialogue(
                    "Village Elder", mutableListOf<String?>(
                        "The prophecy awakens, young warrior...",
                        "Darkness stirs once more in our land."
                    )
                )
            }

            StoryStage.THE_LOST_SWORD -> {
                currentDialogue = Dialogue(
                    "Old Samurai", mutableListOf<String?>(
                        "You must find the lost katana deep within the Forbidden Forest.",
                        "Only then can you challenge the Shin Shogun."
                    )
                )
            }

            StoryStage.THE_SHIN_SHOGUN_RETURN -> {
                currentDialogue = Dialogue(
                    "Messenger", mutableListOf<String?>(
                        "The Shin Shogun grows stronger every day.",
                        "Prepare yourself for the coming battle."
                    )
                )
            }

            StoryStage.THE_FINAL_CONFRONTATION -> {
                currentDialogue = Dialogue(
                    "Your Spirit", mutableListOf<String?>(
                        "This is it. Face the darkness and end this curse!"
                    )
                )
            }

            StoryStage.NEW_DAWN -> {
                currentDialogue = Dialogue(
                    "Village Elder", mutableListOf<String?>(
                        "Peace has returned, but the world will always need heroes."
                    )
                )
            }
        }
    }

    fun clearDialogue() {
        currentDialogue = null
    }

    val activeQuests: MutableCollection<Quest?>
        get() {
            // Filter quests by current stage or all uncompleted quests
            // For simplicity, return all uncompleted quests
            val active: MutableList<Quest?> = ArrayList<Quest?>()
            for (q in quests.values) {
                if (!q.isCompleted()) active.add(q)
            }
            return active
        }

    init {
        initQuests()
    }

    fun getQuest(id: String?): Quest? {
        return quests[id]
    }


    // Helper Codec wrapper classes for Quest and Dialogue
    class QuestCodec(quest: Quest) : Codec<Quest?> {
        val id: String?
        val description: String?
        val completed: Boolean

        init {
            this.id = quest.id
            this.description = quest.description
            this.completed = quest.isCompleted()
        }

        override fun <T> decode(dynamicOps: DynamicOps<T?>?, t: T?): DataResult<Pair<Quest?, T?>?>? {
            return null
        }

        override fun <T> encode(quest: Quest?, dynamicOps: DynamicOps<T?>?, t: T?): DataResult<T?>? {
            return null
        }

        companion object {
            val CODEC: Codec<Quest?> =
                RecordCodecBuilder.create<Quest?>(Function { instance: RecordCodecBuilder.Instance<Quest?>? ->
                    instance!!.group<String?, String?, Boolean?>(
                        Codec.STRING.fieldOf("id").forGetter<Quest?> { obj: Quest? -> obj!!.id },
                        Codec.STRING.fieldOf("description")
                            .forGetter<Quest?> { obj: Quest? -> obj!!.description },
                        Codec.BOOL.fieldOf("completed")
                            .forGetter<Quest?> { obj: Quest? -> obj!!.isCompleted() }
                    ).apply<Quest?>(instance) { id: String?, description: String?, completed: Boolean? ->
                        val q = Quest(id, description) { false } // dummy condition, to be restored
                        q.setCompleted(completed!!)
                        q
                    }
                }
                )
        }
    }


    class DialogueCodec(dialogue: Dialogue) : Codec<Dialogue> {
        val speaker: String?
        val lines: MutableList<String?>?
        val currentLine: Int

        init {
            this.speaker = dialogue.speaker
            this.lines = dialogue.lines
            this.currentLine = dialogue.currentLine
        }

        override fun <T> decode(dynamicOps: DynamicOps<T?>?, t: T?): DataResult<Pair<Dialogue?, T?>?>? {
            return null
        }

        override fun <T> encode(dialogue: Dialogue?, dynamicOps: DynamicOps<T?>?, t: T?): DataResult<T?>? {
            return null
        }

        companion object {
            // Factory for codec deserialization
            val CODEC =
                RecordCodecBuilder.create<Dialogue?>(Function { instance: RecordCodecBuilder.Instance<Dialogue?>? ->
                    instance!!.group<String?, MutableList<String?>?, Int?>(
                        Codec.STRING.fieldOf("speaker")
                            .forGetter<Dialogue?>(Function { d: Dialogue? -> d!!.speaker }),
                        Codec.STRING.listOf().fieldOf("lines")
                            .forGetter<Dialogue?> { d: Dialogue? -> d!!.lines },
                        Codec.INT.fieldOf("currentLine")
                            .forGetter<Dialogue?> { d: Dialogue? -> d!!.currentLine }
                    ).apply<Dialogue?>(
                        instance
                    ) { speaker: String?, lines: MutableList<String?>?, currentLine: Int? ->
                        val d = Dialogue(speaker, lines!!)
                        d.currentLine = currentLine!!
                        d
                    }
                }
                )
        }
    }

    companion object {
        // ======= Save / Load with Codec =======
        val CODEC: Codec<StoryManager?> =
            RecordCodecBuilder.create<StoryManager?>(Function { instance: RecordCodecBuilder.Instance<StoryManager?>? ->
                instance!!.group<String?, MutableMap<String, Quest>, Optional<Dialogue?>?>(
                    Codec.STRING.fieldOf("currentStage")
                        .forGetter<StoryManager?> { story: StoryManager? -> story!!.currentStage.name },
                    Codec.unboundedMap<String, Quest>(Codec.STRING, QuestCodec.Companion.CODEC).fieldOf("quests")
                        .forGetter<StoryManager?> { story: StoryManager? -> story!!.quests },
                    DialogueCodec.CODEC.optionalFieldOf("currentDialogue")
                        .forGetter { story -> Optional.ofNullable(story?.currentDialogue) }
                ).apply<StoryManager?>(
                    instance
                ) { stageName: String?, questsMap: MutableMap<String, Quest>, dialogueOpt: Optional<Dialogue?>? ->
                    val sm = StoryManager()
                    sm.currentStage = StoryStage.valueOf(stageName!!)

                    sm.quests.clear()
                    sm.quests.putAll(questsMap)
                    sm.currentDialogue = dialogueOpt!!.orElse(null)
                    sm
                }
            }
            )
    }
}

