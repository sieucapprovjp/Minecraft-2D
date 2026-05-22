package com.main.game.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.Constants;

public class Chunk {
    public final int chunkX;
    public final int chunkY;
    private final AbstractBlock[][] blocks;

    public Chunk(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        // Mỗi chunk chỉ chứa một mảng 16x16
        this.blocks = new AbstractBlock[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    }

    private void validateLocalCoordinates(int localX, int localY) {
        if (localX < 0 || localX >= Constants.CHUNK_SIZE || localY < 0 || localY >= Constants.CHUNK_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid local block coordinates (" + localX + ", " + localY + ") for chunk ("
                            + chunkX + ", " + chunkY + "). Expected each coordinate to be in [0, "
                            + Constants.CHUNK_SIZE + ").");
        }
    }

    public void setBlock(int localX, int localY, AbstractBlock block) {
        validateLocalCoordinates(localX, localY);
        blocks[localX][localY] = block;
    }

    public AbstractBlock getBlock(int localX, int localY) {
        validateLocalCoordinates(localX, localY);
        return blocks[localX][localY];
    }

    // Tự mỗi chunk biết cách vẽ các block bên trong nó
    public void render(SpriteBatch batch) {
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                if (blocks[x][y] != null) {
                    blocks[x][y].render(batch);
                }
            }
        }
    }
}
