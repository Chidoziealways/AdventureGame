package net.adventuregame.story

import com.adv.core.font.GUIText
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.cutscene.Cutscene
import net.adventuregame.cutscene.CutsceneLine
import net.adventuregame.game.AdventureGame
import net.adventuregame.game.GameState
import net.adventuregame.guis.GuiTexture
import net.adventuregame.player.Player
import org.joml.Vector2f
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class StoryManager {
    var currentStage: StoryStage = StoryStage.THE_AWAKENING
        private set
    private val quests: MutableMap<String?, Quest> = LinkedHashMap<String?, Quest>()
    var currentDialogue: Dialogue? = null
        private set

    val player: Player
        get() = GameState.getInstance().player

    fun addQuest(id: String, quest: Quest) {
        quests[id] = quest
        println("Quest Added: $id")
        println("Total Quests: ${quests.keys}")
    }

    private fun initQuests() {
        // Define quests per stage, you can expand these with real game logic conditions
        quests["find_lost_sword"] = Quest(
            "find_lost_sword",
            "Find the legendary katana in the Forbidden Forest."
        ) { player.hasItemByName("katana") }.apply {
            onComplete = { GameState.getInstance().playCutscene(Cutscene(
                null,
                listOf(
                    CutsceneLine(
                        "やった！刀を見つけた！!"
                    )
                )
            )) }
        }
    }

    fun giveQuest(questId: String) {
        val quest = getQuest(questId)
        if (quest != null && !quest.isCompleted()) {
            // Add to active quests (already handled by StoryManager via quests map)
            println("[QUEST ASSIGNED] ${quest.description}")
            player.currentQuest = quest
            activeQuests.add(quest)
        } else {
            println("[QUEST ERROR] Quest $questId not found or already completed")
        }
    }

    val activeQuests: MutableCollection<Quest> = mutableSetOf()

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

