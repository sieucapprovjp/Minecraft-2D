package com.main.game.utilityblock.jukebox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.blocks.AbstractBlock;
import com.main.game.inventory.ItemRegistry;
import com.main.game.items.DroppedItemManager;
import com.main.game.items.HarvestEntry;
import com.main.game.world.World;

import java.util.HashMap;
import java.util.Map;

public class JukeboxManager {

    private final Map<Integer, String> insertedDiscs = new HashMap<>();

    public boolean hasDisc(World world, int tileX, int tileY) {
        return getDisc(world, tileX, tileY) != null;
    }

    public String getDisc(World world, int tileX, int tileY) {
        if (world == null) {
            return null;
        }
        return insertedDiscs.get(toKey(world, tileX, tileY));
    }

    public boolean insertDisc(World world, int tileX, int tileY, String discItemId) {
        if (world == null || discItemId == null || hasDisc(world, tileX, tileY)) {
            return false;
        }
        insertedDiscs.put(toKey(world, tileX, tileY), discItemId);
        return true;
    }

    public String removeDisc(World world, int tileX, int tileY) {
        if (world == null) {
            return null;
        }
        return insertedDiscs.remove(toKey(world, tileX, tileY));
    }

    public String dropDisc(AbstractBlock block, World world, DroppedItemManager droppedItemManager) {
        if (block == null || world == null || droppedItemManager == null) {
            return null;
        }
        String discItemId = removeDisc(world, block.getTileX(), block.getTileY());
        if (discItemId == null) {
            return null;
        }
        spawnDiscDrop(world, block.getTileX(), block.getTileY(), discItemId, droppedItemManager);
        return discItemId;
    }

    public void spawnDiscDrop(World world, int tileX, int tileY,
                              String discItemId, DroppedItemManager droppedItemManager) {
        if (world == null || discItemId == null || droppedItemManager == null) {
            return;
        }
        TextureRegion texture = ItemRegistry.getTexture(discItemId);
        HarvestEntry entry = new HarvestEntry(
            HarvestEntry.toTileIdx(tileX, tileY, world),
            discItemId,
            texture,
            1,
            MathUtils.random(-0.1f, 0.1f),
            HarvestEntry.RANDOM_VERTICAL_SPEED
        );
        droppedItemManager.spawn(entry, world);
    }

    public void clear() {
        insertedDiscs.clear();
    }

    private int toKey(World world, int tileX, int tileY) {
        return HarvestEntry.toTileIdx(tileX, tileY, world);
    }
}
