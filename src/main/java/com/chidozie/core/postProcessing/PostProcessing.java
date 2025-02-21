package com.chidozie.core.postProcessing;

import com.chidozie.core.gaussianBlur.HorizontalBlur;
import com.chidozie.core.gaussianBlur.VerticalBlur;
import com.chidozie.core.renderEngine.Loader;
import com.chidozie.core.renderEngine.WindowManager;
import net.adventuregame.bloom.BrightFilter;
import net.adventuregame.bloom.CombineFilter;
import net.adventuregame.game.AdventureMain;
import net.adventuregame.models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static CombineFilter combineFilter;

	private static WindowManager window;

	public static void init(Loader loader){
		window = AdventureMain.getWindow();
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		brightFilter = new BrightFilter(window.getWidth() / 2, window.getHeight() / 2);
		hBlur = new HorizontalBlur(window.getWidth() / 5, window.getHeight() / 5);
		vBlur = new VerticalBlur(window.getWidth() / 5, window.getHeight() / 5);
		combineFilter = new CombineFilter();
	}
	
	public static void doPostProcessing(int colourTexture, int brightTexture){
		start();
		//brightFilter.render(colourTexture);
		hBlur.render(brightTexture);
		vBlur.render(hBlur.getOutputTexture());
		combineFilter.render(colourTexture, vBlur.getOutputTexture());
		end();
	}
	
	public static void cleanUp(){
		contrastChanger.cleanUp();
		brightFilter.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		combineFilter.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
