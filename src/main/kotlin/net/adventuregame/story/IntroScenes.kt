package net.adventuregame.story

import net.adventuregame.characters.Person
import net.adventuregame.cutscene.Cutscene
import net.adventuregame.guis.GuiTexture
import org.joml.Vector3f

object IntroScenes {
    fun getIntroForJapan(bg: GuiTexture): Cutscene {
        val mentor = Person("武士の師", "male", Vector3f(0f, 0f, 0f))
        return Cutscene(
            background = bg,
            lines = listOf(
                mentor.talk("お前は日本の侍として生まれた。", "japanese"),
                mentor.talk("刀を持ち、祖国を守る覚悟はあるか？", "japanese")
            )
        )
    }

    fun getIntroForKorea(bg: GuiTexture): Cutscene {
        val mentor = Person("한국의 장군", "male", Vector3f(0f, 0f, 0f))
        return Cutscene(
            background = bg,
            lines = listOf(
                mentor.talk("너는 조선의 전사로 태어났다.", "korean"),
                mentor.talk("조국을 지키고 싸울 준비가 되었는가?", "korean")
            )
        )
    }
}
