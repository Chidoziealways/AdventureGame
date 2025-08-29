package com.adv.core.postProcessing

import com.adv.core.gaussianBlur.HorizontalBlur
import com.adv.core.gaussianBlur.VerticalBlur
import com.adv.core.renderEngine.Loader
import com.adv.core.renderEngine.WindowManager
import net.adventuregame.bloom.BrightFilter
import net.adventuregame.bloom.CombineFilter
import net.adventuregame.game.AdventureMain
import net.adventuregame.models.RawModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

object PostProcessing {
    private val POSITIONS = floatArrayOf(-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
    private var quad: RawModel? = null
    private var contrastChanger: ContrastChanger? = null
    private var brightFilter: BrightFilter? = null
    private var hBlur: HorizontalBlur? = null
    private var vBlur: VerticalBlur? = null
    private var combineFilter: CombineFilter? = null

    private var window: WindowManager? = null

    fun init(loader: Loader) {
        window = AdventureMain.window
        quad = loader.loadToVAO(POSITIONS, 2)
        contrastChanger = ContrastChanger()
        brightFilter = BrightFilter(window!!.width / 2, window!!.height / 2)
        hBlur = HorizontalBlur(window!!.width / 5, window!!.height / 5)
        vBlur = VerticalBlur(window!!.width / 5, window!!.height / 5)
        combineFilter = CombineFilter()
    }

    fun doPostProcessing(colourTexture: Int, brightTexture: Int) {
        start()
        //brightFilter.render(colourTexture);
        hBlur!!.render(brightTexture)
        vBlur!!.render(hBlur!!.outputTexture)
        combineFilter!!.render(colourTexture, vBlur!!.outputTexture)
        end()
    }

    fun cleanUp() {
        contrastChanger!!.cleanUp()
        brightFilter!!.cleanUp()
        hBlur!!.cleanUp()
        vBlur!!.cleanUp()
        combineFilter!!.cleanUp()
    }

    private fun start() {
        GL30.glBindVertexArray(quad!!.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
    }

    private fun end() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
    }
}
