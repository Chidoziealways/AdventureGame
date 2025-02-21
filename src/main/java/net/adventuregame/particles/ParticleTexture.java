package net.adventuregame.particles;

public class ParticleTexture {

    private int textureId;
    private int numberOfRows;

    private boolean additive;

    public ParticleTexture(int textureId, int numberOfRows, boolean additive) {
        this.textureId = textureId;
        this.numberOfRows = numberOfRows;
        this.additive = additive;
    }

    public int getTextureId() {
        return textureId;
    }

    public boolean isAdditive() {
        return additive;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }
}
