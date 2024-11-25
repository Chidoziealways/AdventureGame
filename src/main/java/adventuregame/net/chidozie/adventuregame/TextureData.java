package adventuregame.net.chidozie.adventuregame;

import java.nio.ByteBuffer;

public class TextureData {
    public int width;
    public int height;
    public ByteBuffer data;

    public TextureData(int width, int height, ByteBuffer data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }
}
