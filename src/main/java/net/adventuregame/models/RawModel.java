package net.adventuregame.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawModel {

    private static final Logger log = LoggerFactory.getLogger(RawModel.class);
    private int vaoId, vertexCount;

    public RawModel(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        log.info("CREATED A RAW MODEL");
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
