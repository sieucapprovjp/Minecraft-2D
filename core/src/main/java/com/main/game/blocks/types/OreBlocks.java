package com.main.game.blocks.types;

import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.TextureManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class OreBlocks {}

class CoalOreBlock extends AbstractBlock {
    public CoalOreBlock(int x, int y) { super(x, y, "coal_ore", true, true, 3f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("coal_ore"); }
}

class IronOreBlock extends AbstractBlock {
    public IronOreBlock(int x, int y) { super(x, y, "iron_ore", true, true, 3f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("iron_ore"); }
}

class GoldOreBlock extends AbstractBlock {
    public GoldOreBlock(int x, int y) { super(x, y, "gold_ore", true, true, 3f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("gold_ore"); }
}

class DiamondOreBlock extends AbstractBlock {
    public DiamondOreBlock(int x, int y) { super(x, y, "diamond_ore", true, true, 5f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("diamond_ore"); }
}

class EmeraldOreBlock extends AbstractBlock {
    public EmeraldOreBlock(int x, int y) { super(x, y, "emerald_ore", true, true, 3f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("emerald_ore"); }
}

class LapisOreBlock extends AbstractBlock {
    public LapisOreBlock(int x, int y) { super(x, y, "lapis_ore", true, true, 3f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("lapis_ore"); }
}

class RedstoneOreBlock extends AbstractBlock {
    public RedstoneOreBlock(int x, int y) { super(x, y, "redstone_ore", true, true, 3f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("redstone_ore"); }
}

class CopperOreBlock extends AbstractBlock {
    public CopperOreBlock(int x, int y) { super(x, y, "copper_ore", true, true, 3f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("copper_ore"); }
}

class NetherQuartzOreBlock extends AbstractBlock {
    public NetherQuartzOreBlock(int x, int y) { super(x, y, "nether_quartz", true, true, 2f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("nether_quartz"); }
}

class AncientDebrisBlock extends AbstractBlock {
    public AncientDebrisBlock(int x, int y) { super(x, y, "ancient_debris", true, true, 10f); }
    @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("ancient_debris"); }
}
