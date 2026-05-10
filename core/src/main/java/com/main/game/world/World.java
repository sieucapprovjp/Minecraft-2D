package com.main.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;
import com.main.game.utils.Constants;

import java.util.Random;

public class World {

    private final AbstractBlock[][] blocks;
    public final int width;
    public final int height;

    public World() {
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;
        this.blocks = new AbstractBlock[width][height];
    }

    public AbstractBlock getBlock(int x, int y) {
        if (!isInBounds(x, y)) {
            return null;
        }
        return blocks[x][y];
    }

    public void setBlock(int x, int y, AbstractBlock block) {
        if (!isInBounds(x, y)) {
            return;
        }
        blocks[x][y] = block;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isSolid(int x, int y) {
        AbstractBlock block = getBlock(x, y);
        return block != null && block.isSolid();
    }

    public int getSurfaceY(int x) {
        if (x < 0 || x >= width) {
            return 0;
        }

        for (int y = height - 1; y >= 0; y--) {
            AbstractBlock block = getBlock(x, y);
            if (block == null || !block.isSolid()) {
                continue;
            }
            String blockId = block.getBlockId();
            if ("wood".equals(blockId) || "leaves".equals(blockId)) {
                continue;
            }
            boolean airAbove = y == height - 1 || !isSolid(x, y + 1);
            if (airAbove) {
                return y;
            }
        }
        return 0;
    }

    public void generate(long seed) {
        Random random = new Random(seed);
        int baseGround = height / 2;

        for (int x = 0; x < width; x++) {
            int surface = baseGround
                + (int) (Math.sin(x * 0.10f) * 3f)
                + (int) (Math.sin(x * 0.025f + 1.5f) * 8f)
                + random.nextInt(3) - 1;

            surface = Math.max(8, Math.min(height - 4, surface));

            for (int y = 0; y <= surface; y++) {
                AbstractBlock block;
                if (y == 0) {
                    block = new SimpleBlock(x, y, "bedrock", true, false, 999f, BlockPalette.BEDROCK);
                } else if (y == surface) {
                    boolean isSandPatch = x % 37 < 5;
                    if (isSandPatch) {
                        block = new SimpleBlock(x, y, "sand", true, true, 0.5f, BlockPalette.SAND);
                    } else {
                        block = new SimpleBlock(x, y, "grass", true, true, 0.6f, BlockPalette.GRASS);
                    }
                } else if (y >= surface - 3) {
                    block = new SimpleBlock(x, y, "dirt", true, true, 0.7f, BlockPalette.DIRT);
                } else {
                    block = new SimpleBlock(x, y, "stone", true, true, 1.2f, BlockPalette.STONE);
                }
                setBlock(x, y, block);
            }

            if (x > 2 && x < width - 2 && x % 29 == 0 && random.nextFloat() < 0.65f) {
                int trunkBaseY = surface + 1;
                int trunkHeight = 3 + random.nextInt(2);

                for (int ty = 0; ty < trunkHeight && trunkBaseY + ty < height; ty++) {
                    setBlock(x, trunkBaseY + ty,
                        new SimpleBlock(x, trunkBaseY + ty, "wood", true, true, 0.9f, BlockPalette.WOOD));
                }

                int leafY = trunkBaseY + trunkHeight;
                for (int lx = x - 1; lx <= x + 1; lx++) {
                    for (int ly = leafY - 1; ly <= leafY; ly++) {
                        if (isInBounds(lx, ly) && getBlock(lx, ly) == null) {
                            setBlock(lx, ly,
                                new SimpleBlock(lx, ly, "leaves", false, true, 0.2f, BlockPalette.LEAVES));
                        }
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        int minX = Math.max(0, (int) Math.floor(camera.position.x - camera.viewportWidth / 2f) - 1);
        int maxX = Math.min(width - 1, (int) Math.ceil(camera.position.x + camera.viewportWidth / 2f) + 1);
        int minY = Math.max(0, (int) Math.floor(camera.position.y - camera.viewportHeight / 2f) - 1);
        int maxY = Math.min(height - 1, (int) Math.ceil(camera.position.y + camera.viewportHeight / 2f) + 1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                AbstractBlock block = blocks[x][y];
                if (block != null) {
                    block.render(batch);
                }
            }
        }
    }
}
