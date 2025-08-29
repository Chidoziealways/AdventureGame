val cutsceneBg = GuiTexture(loader.loadGameTexture("title/title_adventure_game"), Vector2f(60f,0f), Vector2f(1f,1f))

        val cutscene = when (chosenNation){
            Nation.JAPAN -> IntroScenes.getIntroForJapan(cutsceneBg)
            Nation.KOREA -> IntroScenes.getIntroForKorea(cutsceneBg)
            else -> null
        }

        playCutscene(cutscene!!)
