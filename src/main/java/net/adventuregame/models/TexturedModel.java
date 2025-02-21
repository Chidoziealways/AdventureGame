package net.adventuregame.models;

import com.chidozie.core.textures.ModelTexture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TexturedModel {

    private static final Logger log = LoggerFactory.getLogger(TexturedModel.class);
    private RawModel rawModel;
    private ModelTexture texture;

    public TexturedModel(RawModel rawModel, ModelTexture texture) {
        this.rawModel = rawModel;
        this.texture = texture;
        log.info("Created Textured Model With");
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return texture;
    }
}
