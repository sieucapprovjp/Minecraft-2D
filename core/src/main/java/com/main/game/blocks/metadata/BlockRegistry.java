package com.main.game.blocks.metadata;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.inventory.ToolRegistry;
import com.main.game.utils.TextureManager;
import com.main.game.world.BlockPalette;

import java.util.HashMap;
import java.util.Map;

public final class BlockRegistry {

    private static final Map<String, BlockDefinition> BLOCKS = new HashMap<>();
    private static final BlockDefinition DEFAULT_BLOCK = BlockDefinition.builder("unknown")
        .textureName(null)
        .paletteFallback(BlockDefinition.PaletteFallback.STONE)
        .preferPalette()
        .build();

    static {
        registerPalette("grass", BlockDefinition.PaletteFallback.GRASS, 0.6f, true);
        registerPalette("dirt", BlockDefinition.PaletteFallback.DIRT, 0.6f, true);
        registerPalette("stone", BlockDefinition.PaletteFallback.STONE, 1.2f, true, "cobblestone", 1);
        registerTexture("deepslate", "deepslate", BlockDefinition.PaletteFallback.STONE, 1.8f, true, null, 1, false);
        registerPalette("bedrock", BlockDefinition.PaletteFallback.BEDROCK, 999f, false);
        registerPalette("sand", BlockDefinition.PaletteFallback.SAND, 0.6f, true);
        registerPalette("wood", BlockDefinition.PaletteFallback.WOOD, 0.9f, true);
        registerPalette("leaves", BlockDefinition.PaletteFallback.LEAVES, 0.2f, true, false);
        registerPalette("planks", BlockDefinition.PaletteFallback.PLANKS, 0.6f, true);
        registerPalette("snow", BlockDefinition.PaletteFallback.SNOW, 0.2f, true);
        registerPalette("ice", BlockDefinition.PaletteFallback.ICE, 0.4f, true);
        registerPalette("sandstone", BlockDefinition.PaletteFallback.SANDSTONE, 1.2f, true, null, 1);
        registerPalette("cactus", BlockDefinition.PaletteFallback.CACTUS, 0.9f, true);

        registerTexture("crafting_table", "crafting_table", BlockDefinition.PaletteFallback.PLANKS, 0.9f, true, null, 0, false);
        registerTexture("furnace", "furnace_off", BlockDefinition.PaletteFallback.NONE, 3.5f, true, null, 1, false);
        registerTexture("chest", "chest_closed", BlockDefinition.PaletteFallback.NONE, 0.9f, true, null, 0, false);

        registerOre("coal_ore", "coal", 3f, 1);
        registerOre("deepslate_co", "coal", 3f, 1);
        registerOre("diamond_ore", "diamond", 5f, 3);
        registerOre("deepslate_do", "diamond", 5f, 3);
        registerOre("lapis_ore", "lapis", 3f, 2);
        registerOre("ore_lapis_deepslate", "lapis", 3f, 2);
        registerOre("redstone_ore", "redstone", 3f, 2);
        registerOre("deepslate_ro", "redstone", 3f, 2);
        registerOre("emerald_ore", "emerald", 3f, 3);
        registerOre("deepslate_eo", "emerald", 3f, 3);
        registerOre("iron_ore", "raw_iron", 3f, 2);
        registerOre("deepslate_io", "raw_iron", 3f, 2);
        registerOre("gold_ore", "raw_gold", 3f, 2);
        registerOre("deepslate_go", "raw_gold", 3f, 2);
        registerOre("copper_ore", "raw_copper", 3f, 1);
        registerOre("deepslate_copper", "raw_copper", 3f, 1);
    }

    private BlockRegistry() {
    }

    public static BlockDefinition get(String blockId) {
        return blockId == null ? null : BLOCKS.get(blockId);
    }

    public static float getHardness(String blockId) {
        return definitionOrDefault(blockId).getHardness();
    }

    public static boolean isSolid(String blockId) {
        return definitionOrDefault(blockId).isSolid();
    }

    public static boolean isBreakable(String blockId) {
        return definitionOrDefault(blockId).isBreakable();
    }

    public static boolean isPlaceable(String blockId) {
        BlockDefinition definition = get(blockId);
        return definition != null && definition.isPlaceable();
    }

    public static boolean isOre(String blockId) {
        BlockDefinition definition = get(blockId);
        return definition != null && definition.isOre();
    }

    public static String getDropItemId(String blockId) {
        BlockDefinition definition = get(blockId);
        return definition == null ? blockId : definition.getDropItemId();
    }

    public static boolean canDrop(String blockId, String heldItemId) {
        int requiredLevel = definitionOrDefault(blockId).getRequiredPickaxeLevel();
        if (requiredLevel <= 0) {
            return true;
        }
        return ToolRegistry.isPickaxe(heldItemId)
            && ToolRegistry.getHarvestLevel(heldItemId) >= requiredLevel;
    }

    public static String getTextureName(String blockId) {
        BlockDefinition definition = get(blockId);
        return definition == null ? null : definition.getTextureName();
    }

    public static TextureRegion getTexture(String blockId) {
        return resolveTexture(definitionOrDefault(blockId));
    }

    public static TextureRegion getPaletteFallbackTexture(String blockId) {
        BlockDefinition definition = get(blockId);
        if (definition == null || !definition.shouldPreferPalette()) {
            return null;
        }
        return paletteTexture(definition.getPaletteFallback());
    }

    private static void register(BlockDefinition definition) {
        BLOCKS.put(definition.getId(), definition);
    }

    private static void registerPalette(String id, BlockDefinition.PaletteFallback paletteFallback,
                                        float hardness, boolean placeable) {
        registerPalette(id, paletteFallback, hardness, placeable, true);
    }

    private static void registerPalette(String id, BlockDefinition.PaletteFallback paletteFallback,
                                        float hardness, boolean placeable, boolean solid) {
        register(BlockDefinition.builder(id)
            .paletteFallback(paletteFallback)
            .preferPalette()
            .hardness(hardness)
            .solid(solid)
            .breakable(!"bedrock".equals(id))
            .placeableIf(placeable)
            .build());
    }

    private static void registerPalette(String id, BlockDefinition.PaletteFallback paletteFallback,
                                        float hardness, boolean placeable, String dropItemId,
                                        int requiredPickaxeLevel) {
        register(BlockDefinition.builder(id)
            .paletteFallback(paletteFallback)
            .preferPalette()
            .hardness(hardness)
            .dropItemId(dropItemId)
            .requiredPickaxeLevel(requiredPickaxeLevel)
            .placeableIf(placeable)
            .build());
    }

    private static void registerTexture(String id, String textureName, BlockDefinition.PaletteFallback fallback,
                                        float hardness, boolean placeable, String dropItemId,
                                        int requiredPickaxeLevel, boolean ore) {
        BlockDefinition.Builder builder = BlockDefinition.builder(id)
            .textureName(textureName)
            .paletteFallback(fallback)
            .hardness(hardness)
            .dropItemId(dropItemId)
            .requiredPickaxeLevel(requiredPickaxeLevel);
        if (placeable) {
            builder.placeable();
        }
        if (ore) {
            builder.ore();
        }
        register(builder.build());
    }

    private static void registerOre(String id, String dropItemId, float hardness, int requiredPickaxeLevel) {
        registerTexture(id, id, BlockDefinition.PaletteFallback.STONE, hardness, true,
            dropItemId, requiredPickaxeLevel, true);
    }

    private static BlockDefinition definitionOrDefault(String blockId) {
        BlockDefinition definition = get(blockId);
        return definition == null ? DEFAULT_BLOCK : definition;
    }

    private static TextureRegion resolveTexture(BlockDefinition definition) {
        if (definition.shouldPreferPalette()) {
            TextureRegion paletteTexture = paletteTexture(definition.getPaletteFallback());
            if (paletteTexture != null) {
                return paletteTexture;
            }
        }

        String textureName = definition.getTextureName();
        if (textureName != null) {
            TextureRegion texture = TextureManager.getInstance().getTexture(textureName);
            if (texture != null) {
                return texture;
            }
        }
        return paletteTexture(definition.getPaletteFallback());
    }

    private static TextureRegion paletteTexture(BlockDefinition.PaletteFallback paletteFallback) {
        if (paletteFallback == BlockDefinition.PaletteFallback.GRASS) return BlockPalette.getGrass();
        if (paletteFallback == BlockDefinition.PaletteFallback.DIRT) return BlockPalette.getDirt();
        if (paletteFallback == BlockDefinition.PaletteFallback.STONE) return BlockPalette.getStone();
        if (paletteFallback == BlockDefinition.PaletteFallback.BEDROCK) return BlockPalette.getBedrock();
        if (paletteFallback == BlockDefinition.PaletteFallback.SAND) return BlockPalette.getSand();
        if (paletteFallback == BlockDefinition.PaletteFallback.WOOD) return BlockPalette.getWood();
        if (paletteFallback == BlockDefinition.PaletteFallback.LEAVES) return BlockPalette.getLeaves();
        if (paletteFallback == BlockDefinition.PaletteFallback.PLANKS) return BlockPalette.getPlanks();
        if (paletteFallback == BlockDefinition.PaletteFallback.SNOW) return BlockPalette.getSnow();
        if (paletteFallback == BlockDefinition.PaletteFallback.ICE) return BlockPalette.getIce();
        if (paletteFallback == BlockDefinition.PaletteFallback.SANDSTONE) return BlockPalette.getSandstone();
        if (paletteFallback == BlockDefinition.PaletteFallback.CACTUS) return BlockPalette.getCactus();
        return null;
    }
}
