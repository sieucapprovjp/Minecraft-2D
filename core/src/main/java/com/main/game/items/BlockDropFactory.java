package com.main.game.items;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.blocks.AbstractBlock;
import com.main.game.inventory.ItemRegistry;
import com.main.game.interaction.BlockHarvestRules;
import com.main.game.world.World;

import java.util.Map;

public final class BlockDropFactory {

    private static final Map<String, String> ORE_DROPS = Map.ofEntries(
        Map.entry("coal_ore", "coal"),
        Map.entry("deepslate_co", "coal"),
        Map.entry("diamond_ore", "diamond"),
        Map.entry("deepslate_do", "diamond"),
        Map.entry("lapis_ore", "lapis"),
        Map.entry("ore_lapis_deepslate", "lapis"),
        Map.entry("redstone_ore", "redstone"),
        Map.entry("deepslate_ro", "redstone"),
        Map.entry("emerald_ore", "emerald"),
        Map.entry("deepslate_eo", "emerald"),
        Map.entry("iron_ore", "raw_iron"),
        Map.entry("deepslate_io", "raw_iron"),
        Map.entry("gold_ore", "raw_gold"),
        Map.entry("deepslate_go", "raw_gold"),
        Map.entry("copper_ore", "raw_copper"),
        Map.entry("deepslate_copper", "raw_copper")
    );

    private BlockDropFactory() {
    }

    public static HarvestEntry createDrop(AbstractBlock block, World world) {
        return createDrop(block, world, null);
    }

    public static HarvestEntry createDrop(AbstractBlock block, World world, String heldItemId) {
        if (block == null) {
            return null;
        }

        String blockId = block.getBlockId();
        if (!BlockHarvestRules.canDrop(blockId, heldItemId)) {
            return null;
        }

        int tileIdx = HarvestEntry.toTileIdx(block.getTileX(), block.getTileY(), world);
        String itemId = dropItemId(blockId);
        TextureRegion texture = ItemRegistry.getTexture(itemId);
        if (texture == null && !ORE_DROPS.containsKey(blockId)) {
            texture = block.getTexture();
        }
        return new HarvestEntry(
            tileIdx,
            itemId,
            texture,
            1,
            MathUtils.random(-0.1f, 0.1f),
            HarvestEntry.RANDOM_VERTICAL_SPEED
        );
    }

    private static String dropItemId(String blockId) {
        String oreDrop = ORE_DROPS.get(blockId);
        if (oreDrop != null) {
            return oreDrop;
        }
        if ("stone".equals(blockId)) {
            return "cobblestone";
        }
        return blockId;
    }
}
