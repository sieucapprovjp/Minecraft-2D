package com.main.game.utilityblock.craftingtable;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.player.Player;
import com.main.game.utilityblock.UtilityBlockInteractionController;
import com.main.game.world.World;

public class CraftingTableInteractionController {

    private static final String CRAFTING_TABLE_ID = "crafting_table";
    private static final String VILLAGE_CRAFTING_TABLE_ID = "village_crafting_table";
    private static final String[] CRAFTING_TABLE_IDS = {
        CRAFTING_TABLE_ID, VILLAGE_CRAFTING_TABLE_ID
    };

    private final UtilityBlockInteractionController interactionController = new UtilityBlockInteractionController();

    public boolean canOpen(Player player, World world, OrthographicCamera camera, Viewport viewport) {
        return interactionController.canOpenAny(CRAFTING_TABLE_IDS, player, world, camera, viewport);
    }
}
