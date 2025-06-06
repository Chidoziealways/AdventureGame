package com.chidozie.core.terrains;

import com.chidozie.core.renderEngine.Loader;
import com.chidozie.core.textures.ModelTexture;
import com.chidozie.core.textures.TerrainTexture;
import com.chidozie.core.textures.TerrainTexturePack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.adventuregame.models.RawModel;
import net.adventuregame.toolbox.Maths;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Terrain {

    private static final float SIZE = 2000;
    private static final float MAX_HEIGHT = 80;
    private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;
    private static final Logger log = LoggerFactory.getLogger(Terrain.class);

    private float x;
    private float z;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;
    private HeightsGenerator generator;

    private float[][] heights;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack,
                   TerrainTexture blendMap, int seed) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        generator = new HeightsGenerator(seed);
        this.model = generateTerrain(loader);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public RawModel getModel() {
        return model;
    }

    public void setModel(RawModel model) {
        this.model = model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
            return  0;
        }
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer;
        if (xCoord <= (1 - zCoord)) {
            answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
                    new Vector2f(xCoord, zCoord));
        }else {
            answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
                    new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return answer;
    }

    private RawModel generateTerrain(Loader loader){
        int VERTEX_COUNT = 256;
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j, i, generator);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, generator);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        log.info("Generated Terrain of " + x + "x" + z);
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
        float heightL = getHeight(x - 1, z, generator);
        float heightR = getHeight(x + 1, z, generator);
        float heightD = getHeight(x, z - 1, generator);
        float heightU = getHeight(x, z + 1, generator);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalize();
        return normal;
    }

    private float getHeight(int x, int z, HeightsGenerator generator) {
        return generator.generateHeight(x, z);
    }

    public int getSeed() {
        return generator.getSeed();
    }

    public void setSeed(int seed) {
        generator.seed = seed;
    }
}
