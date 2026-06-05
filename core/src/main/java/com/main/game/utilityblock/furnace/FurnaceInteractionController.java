package com.main.game.utilityblock.furnace;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.player.Player;
import com.main.game.utilityblock.UtilityBlockInteractionController;
import com.main.game.world.World;

public class FurnaceInteractionController {

    public static final String FURNACE_ID = "furnace";
    public static final String VILLAGE_FURNACE_ID = "village_furnace";
    private static final String[] FURNACE_IDS = {
        FURNACE_ID, VILLAGE_FURNACE_ID
    };

    private final UtilityBlockInteractionController interactionController = new UtilityBlockInteractionController();

    public boolean canOpen(Player player, World world, OrthographicCamera camera, Viewport viewport) {
        return interactionController.canOpenAny(FURNACE_IDS, player, world, camera, viewport);
    }

    public static boolean isFurnaceBlock(String blockId) {
        return FURNACE_ID.equals(blockId) || VILLAGE_FURNACE_ID.equals(blockId);
    }

    public int getHoveredTileX() {
        return interactionController.getHoveredTileX();
    }

    public int getHoveredTileY() {
        return interactionController.getHoveredTileY();
    }
}
