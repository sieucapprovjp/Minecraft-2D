package com.main.game.interaction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.blocks.AbstractBlock;
import com.main.game.entities.player.Player;
import com.main.game.world.World;

public class CraftingTableInteractionController {

    private static final String CRAFTING_TABLE_ID = "crafting_table";
    private static final float OPEN_REACH = 4.5f;

    private final Vector2 mouseWorld = new Vector2();

    public boolean canOpen(Player player, World world, OrthographicCamera camera, Viewport viewport) {
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
        AbstractBlock block = world.getBlock(tileX, tileY);
        return block != null
            && CRAFTING_TABLE_ID.equals(block.getBlockId())
            && isWithinReach(player, tileX, tileY);
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
}
