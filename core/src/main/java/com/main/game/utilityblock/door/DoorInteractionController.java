package com.main.game.utilityblock.door;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.blocks.AbstractBlock;
import com.main.game.entities.player.Player;
import com.main.game.world.World;
import com.main.game.worldgen.WorldBlockFactory;

public class DoorInteractionController {

    public static final String BOTTOM_CLOSED = "village_door_bottom_closed";
    public static final String TOP_CLOSED = "village_door_top_closed";
    public static final String BOTTOM_OPEN = "village_door_bottom_open";
    public static final String TOP_OPEN = "village_door_top_open";

    private static final float OPEN_REACH = 4.5f;

    private final Vector2 mouseWorld = new Vector2();
    private boolean lastToggleOpened;

    public boolean toggleHovered(Player player, World world, OrthographicCamera camera, Viewport viewport) {
        if (player == null
            || world == null
            || camera == null
            || viewport == null
            || !player.isAlive()) {
            return false;
        }

        camera.update();
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);

        int tileX = (int) Math.floor(mouseWorld.x);
        int tileY = (int) Math.floor(mouseWorld.y);
        if (!isWithinReach(player, tileX, tileY)) {
            return false;
        }

        ToggleResult result = toggleDoor(world, tileX, tileY);
        if (!result.toggled) {
            return false;
        }
        lastToggleOpened = result.opened;
        return true;
    }

    public boolean wasLastToggleOpened() {
        return lastToggleOpened;
    }

    public static ToggleResult toggleDoor(World world, int tileX, int tileY) {
        DoorPair pair = findDoorPair(world, tileX, tileY);
        if (pair == null) {
            return ToggleResult.notToggled();
        }

        if (pair.open) {
            setDoorBlocks(world, pair.x, pair.bottomY, BOTTOM_CLOSED, TOP_CLOSED);
            return ToggleResult.closed();
        }
        setDoorBlocks(world, pair.x, pair.bottomY, BOTTOM_OPEN, TOP_OPEN);
        return ToggleResult.opened();
    }

    public static boolean removeDoorPair(World world, AbstractBlock block) {
        if (world == null || block == null || !isDoorBlock(block.getBlockId())) {
            return false;
        }
        DoorPair pair = findDoorPair(world, block.getTileX(), block.getTileY());
        if (pair == null) {
            return false;
        }
        world.setBlock(pair.x, pair.bottomY, null);
        world.setBlock(pair.x, pair.bottomY + 1, null);
        return true;
    }

    public static boolean isDoorBlock(String blockId) {
        return BOTTOM_CLOSED.equals(blockId)
            || TOP_CLOSED.equals(blockId)
            || BOTTOM_OPEN.equals(blockId)
            || TOP_OPEN.equals(blockId);
    }

    private static DoorPair findDoorPair(World world, int tileX, int tileY) {
        if (world == null || !world.isInBounds(tileX, tileY)) {
            return null;
        }
        AbstractBlock block = world.getBlock(tileX, tileY);
        if (block == null) {
            return null;
        }

        String blockId = block.getBlockId();
        if (BOTTOM_CLOSED.equals(blockId)) {
            return hasTop(world, tileX, tileY, TOP_CLOSED) ? new DoorPair(tileX, tileY, false) : null;
        }
        if (TOP_CLOSED.equals(blockId)) {
            return hasBottom(world, tileX, tileY, BOTTOM_CLOSED) ? new DoorPair(tileX, tileY - 1, false) : null;
        }
        if (BOTTOM_OPEN.equals(blockId)) {
            return hasTop(world, tileX, tileY, TOP_OPEN) ? new DoorPair(tileX, tileY, true) : null;
        }
        if (TOP_OPEN.equals(blockId)) {
            return hasBottom(world, tileX, tileY, BOTTOM_OPEN) ? new DoorPair(tileX, tileY - 1, true) : null;
        }
        return null;
    }

    private static boolean hasTop(World world, int x, int bottomY, String expectedTopId) {
        AbstractBlock top = world.getBlock(x, bottomY + 1);
        return top != null && expectedTopId.equals(top.getBlockId());
    }

    private static boolean hasBottom(World world, int x, int topY, String expectedBottomId) {
        AbstractBlock bottom = world.getBlock(x, topY - 1);
        return bottom != null && expectedBottomId.equals(bottom.getBlockId());
    }

    private static void setDoorBlocks(World world, int x, int bottomY, String bottomId, String topId) {
        world.setBlock(x, bottomY, WorldBlockFactory.create(x, bottomY, bottomId));
        world.setBlock(x, bottomY + 1, WorldBlockFactory.create(x, bottomY + 1, topId));
    }

    private boolean isWithinReach(Player player, int tileX, int tileY) {
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;
        float tileCenterX = tileX + 0.5f;
        float tileCenterY = tileY + 0.5f;
        float dx = tileCenterX - playerCenterX;
        float dy = tileCenterY - playerCenterY;
        return dx * dx + dy * dy <= OPEN_REACH * OPEN_REACH;
    }

    public static final class ToggleResult {
        public final boolean toggled;
        public final boolean opened;

        private ToggleResult(boolean toggled, boolean opened) {
            this.toggled = toggled;
            this.opened = opened;
        }

        private static ToggleResult notToggled() {
            return new ToggleResult(false, false);
        }

        private static ToggleResult opened() {
            return new ToggleResult(true, true);
        }

        private static ToggleResult closed() {
            return new ToggleResult(true, false);
        }
    }

    private static final class DoorPair {
        private final int x;
        private final int bottomY;
        private final boolean open;

        private DoorPair(int x, int bottomY, boolean open) {
            this.x = x;
            this.bottomY = bottomY;
            this.open = open;
        }
    }
}
