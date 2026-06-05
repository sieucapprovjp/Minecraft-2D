package com.main.game.utilityblock.chest;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.player.Player;
import com.main.game.utilityblock.UtilityBlockInteractionController;
import com.main.game.world.World;

public class ChestInteractionController {

    public static final String CHEST_ID = "chest";
    public static final String VILLAGE_CHEST_ID = "village_chest";
    private static final String[] CHEST_IDS = {
        CHEST_ID, VILLAGE_CHEST_ID
    };

    private final UtilityBlockInteractionController interactionController = new UtilityBlockInteractionController();

    public boolean canOpen(Player player, World world, OrthographicCamera camera, Viewport viewport) {
        return interactionController.canOpenAny(CHEST_IDS, player, world, camera, viewport);
    }

    public static boolean isChestBlock(String blockId) {
        return CHEST_ID.equals(blockId) || VILLAGE_CHEST_ID.equals(blockId);
    }

    public int getHoveredTileX() {
        return interactionController.getHoveredTileX();
    }

    public int getHoveredTileY() {
        return interactionController.getHoveredTileY();
    }
}
