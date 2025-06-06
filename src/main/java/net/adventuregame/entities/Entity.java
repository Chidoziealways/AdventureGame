package net.adventuregame.entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.adventuregame.codec.TexturedModelCodec;
import net.adventuregame.codec.Vector3fCodec;
import net.adventuregame.models.TexturedModel;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entity {

    private static final Logger log = LoggerFactory.getLogger(Entity.class);
    private TexturedModel model;
    private Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;

    private int textureIndex = 0;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.textureIndex = index;
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public float getTextureXOffset() {
        int column = textureIndex % model.getTexture().getNumberOfRows();
        return (float) column / (float) model.getTexture().getNumberOfRows();
    }

    public float getTextureYOffset() {
        int row = textureIndex / model.getTexture().getNumberOfRows();
        return (float) row / (float) model.getTexture().getNumberOfRows();
    }

    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }



    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public static final Codec<Entity> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TexturedModelCodec.CODEC.fieldOf("model").forGetter(entity -> entity.model),
                    Codec.INT.optionalFieldOf("textureIndex", 0).forGetter(Entity::getTextureIndex),
                    Vector3fCodec.CODEC.fieldOf("position").forGetter(entity -> entity.position),
                    Codec.FLOAT.fieldOf("rotX").forGetter(entity -> entity.rotX),
                    Codec.FLOAT.fieldOf("rotY").forGetter(entity -> entity.rotY),
                    Codec.FLOAT.fieldOf("rotZ").forGetter(entity -> entity.rotZ),
                    Codec.FLOAT.fieldOf("scale").forGetter(entity -> entity.scale)
            ).apply(instance, Entity::new
            )
    );
}
