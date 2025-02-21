package net.adventuregame.game;

import com.chidozie.core.audio.AudioManager;
import com.chidozie.core.audio.Source;
import com.chidozie.core.font.FontType;
import com.chidozie.core.font.GUIText;
import com.chidozie.core.font.TextMaster;
import com.chidozie.core.normalmap.NormalMappedObjLoader;
import com.chidozie.core.postProcessing.Fbo;
import com.chidozie.core.postProcessing.PostProcessing;
import com.chidozie.core.renderEngine.*;
import com.chidozie.core.terrains.Terrain;
import com.chidozie.core.textures.ModelTexture;
import com.chidozie.core.textures.TerrainTexture;
import com.chidozie.core.textures.TerrainTexturePack;
import net.adventuregame.entities.Camera;
import net.adventuregame.entities.Entity;
import net.adventuregame.entities.Light;
import net.adventuregame.entities.Player;
import net.adventuregame.guis.GuiRenderer;
import net.adventuregame.guis.GuiTexture;
import net.adventuregame.models.RawModel;
import net.adventuregame.models.TexturedModel;
import net.adventuregame.particles.Particle;
import net.adventuregame.particles.ParticleSystem;
import net.adventuregame.particles.ParticleTexture;
import net.adventuregame.particles.粒子の先生;
import net.adventuregame.sounds.Music;
import net.adventuregame.toolbox.MousePicker;
import net.adventuregame.water.WaterFrameBuffers;
import net.adventuregame.water.WaterRenderer;
import net.adventuregame.water.WaterShader;
import net.adventuregame.water.WaterTile;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Main Class For my Adventure Game
 * @author Chidozie Derek Chidozie-Uzowulu
 */

public class AdventureMain {
    private static final Logger log = LoggerFactory.getLogger(AdventureMain.class);
    private static WindowManager window;

    /**
     * The Main Method
     * @param args --I don't know what this is for.
     */

    public static void main(String[] args) {
        System.out.println(log.isInfoEnabled() + " " + log.isErrorEnabled() + " " + log.isDebugEnabled() + " " + log.isTraceEnabled() + " " + log.isWarnEnabled());
        window = new WindowManager("ADVENTURE GAME", false);

        AdventureGame game = new AdventureGame(window);
        game.run();
    }

    public static WindowManager getWindow() {
        return window;
    }
}
