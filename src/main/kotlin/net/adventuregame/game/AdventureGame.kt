package net.adventuregame.game

import com.adv.core.font.FontManager
import com.adv.core.font.TextMaster
import com.adv.core.renderEngine.Loader
import com.adv.core.renderEngine.WindowManager
import net.adventuregame.guis.GuiTexture
import net.adventuregame.story.IntroScenes
import org.joml.Vector2f

class AdventureGame(private val window: WindowManager) {
    private val init: GameInit
    private val loop: GameLoop
    companion object {
        val loader = Loader()

        val fontManager = FontManager()

        val japaneseFont = fontManager.loadFont(
            name = "japanese",
            path = "japanese",
            size = 32f,
            ranges = listOf(
                0x0020..0x007E,    // Basic Latin
                0x3040..0x309F,    // Hiragana
                0x30A0..0x30FF,    // Katakana
                0x31F0..0x31FF,    // Katakana Phonetic Extensions
                0x4E00..0x9FFF     // CJK Unified Ideographs (Kanji subset)
            )
        )

        val koreanFont = fontManager.loadFont(
            name = "korean",
            path = "korean",
            size = 32f,
            ranges = listOf(
                0x0020..0x007E,    // Basic Latin
                0x1100..0x11FF,    // Hangul Jamo
                0x3130..0x318F,    // Hangul Compatibility Jamo
                0xAC00..0xD7AF     // Hangul Syllables
            )
        )
    }

    init {
        this.init = GameInit(window)
        this.loop = GameLoop(init.state!!)
    }

    fun run() {
        // Title screen first
        val titleScreen = TitleScreen(window, loader)
        titleScreen.run()

        if (!titleScreen.shouldStartGame) return  // user exited

        val loadingScreen = LoadingScreen(window, loader, init)
        loadingScreen.run()

        val player = init.state!!.player

        if (player.chosenNation == null) {
            val selectScreen = NationSelectScreen(window, loader)
            selectScreen.run()
            val nation = selectScreen.chosenNation ?: return
            player.chosenNation = nation

            if (!player.seenIntro) {

                val cutsceneBg = GuiTexture(
                    loader.loadGameTexture("title/title_adventure_game"),
                    Vector2f(60f, 0f), Vector2f(1f, 1f)
                )

                val cutscene = when (player.chosenNation) {
                    Nation.JAPAN -> IntroScenes.getIntroForJapan(cutsceneBg)
                    Nation.KOREA -> IntroScenes.getIntroForKorea(cutsceneBg)
                    else -> null
                }

                if (cutscene != null) {
                    init.state.playCutscene(cutscene, 500)
                }


                if (player.chosenNation == Nation.JAPAN) {
                    println("お前は日本の侍だぞ!")
                    player.seenIntro = true
                    GameState.storyManager?.giveQuest("find_lost_sword")
                } else if (player.chosenNation == Nation.KOREA) {
                    println("너는 한국의 전사다!")
                    player.seenIntro = true
                }
            }
        }

        // 2) Enter the perpetual game loop
        TextMaster.init(loader)
        loop.run()
    }
}
