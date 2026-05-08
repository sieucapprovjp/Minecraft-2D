package com.main.game.world;

import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.TextureManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// IMPORT CỤ THỂ TỪNG CLASS CHA ĐỂ MÁY KHÔNG NHẦM LÀ PACKAGE
import com.main.game.blocks.types.UtilityBlocks;
import com.main.game.blocks.types.NatureBlocks;
import com.main.game.blocks.types.StoneBlocks;
import com.main.game.blocks.types.WoodBlocks;

public final class BlockPalette {

    // Giữ các biến này để GameScreen/World hết báo đỏ
    public static final TextureRegion GRASS   = TextureManager.getInstance().getTexture("grass_block");
    public static final TextureRegion DIRT    = TextureManager.getInstance().getTexture("dirt");
    public static final TextureRegion STONE   = TextureManager.getInstance().getTexture("stone");
    public static final TextureRegion BEDROCK = TextureManager.getInstance().getTexture("bedrock");
    public static final TextureRegion SAND    = TextureManager.getInstance().getTexture("sand");
    public static final TextureRegion WOOD    = TextureManager.getInstance().getTexture("oak_log");
    public static final TextureRegion LEAVES  = TextureManager.getInstance().getTexture("oak_leaves");
    public static final TextureRegion PLANKS  = TextureManager.getInstance().getTexture("oak_planks");

    private BlockPalette() {}

    public static AbstractBlock getBlockByInt(int id, int x, int y) {
        switch (id) {
            // Dùng cấu trúc: TênClassCha.TênClassCon
            case 0:  return new UtilityBlocks.AirBlock(x, y);
            case 1:  return new NatureBlocks.GrassBlockBlock(x, y);
            case 2:  return new NatureBlocks.DirtBlock(x, y);
            case 3:  return new StoneBlocks.StoneBlock(x, y);
            case 4:  return new WoodBlocks.OakLogBlock(x, y);
            case 5:  return new WoodBlocks.OakPlanksBlock(x, y);
            case 6:  return new WoodBlocks.OakLeavesBlock(x, y);
            case 7:  return new StoneBlocks.BedrockBlock(x, y);
            case 9:  return new NatureBlocks.SandBlock(x, y);

            default: return new UtilityBlocks.AirBlock(x, y);
        }
    }
    public static void dispose() {}
}
