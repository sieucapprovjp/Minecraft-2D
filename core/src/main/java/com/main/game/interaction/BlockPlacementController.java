package com.main.game.interaction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.player.Player;
import com.main.game.inventory.ItemRegistry;
import com.main.game.world.World;
import com.main.game.worldgen.WorldBlockFactory;

public class BlockPlacementController {

    private static final float PLACE_REACH = 4.5f;

    private final Vector2 mouseWorld = new Vector2();
    private int hoveredPlaceX = -1;
    private int hoveredPlaceY = -1;
    private BlockPlacementListener blockPlacementListener;

    public boolean update(Player player, World world, OrthographicCamera camera, Viewport viewport,
                          String heldItemId, boolean inputBlocked) {
        updateHover(player, world, camera, viewport, heldItemId, inputBlocked);

        if (!Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)
            || !hasHoveredPlacement()
            || world == null
            || !ItemRegistry.isPlaceableBlock(heldItemId)) {
            return false;
        }

        world.setBlock(hoveredPlaceX, hoveredPlaceY,
            WorldBlockFactory.create(hoveredPlaceX, hoveredPlaceY, heldItemId));
        if (blockPlacementListener != null) {
            blockPlacementListener.onBlockPlaced(heldItemId, hoveredPlaceX, hoveredPlaceY);
        }
        return true;
    }

    public void setBlockPlacementListener(BlockPlacementListener blockPlacementListener) {
        this.blockPlacementListener = blockPlacementListener;
    }

    public boolean hasHoveredPlacement() {
        return hoveredPlaceX >= 0 && hoveredPlaceY >= 0;
    }

    public int getHoveredPlaceX() {
        return hoveredPlaceX;
    }

    public int getHoveredPlaceY() {
        return hoveredPlaceY;
    }

    private void updateHover(Player player, World world, OrthographicCamera camera, Viewport viewport,
                             String heldItemId, boolean inputBlocked) {
        hoveredPlaceX = -1;
        hoveredPlaceY = -1;

        if (inputBlocked
            || player == null
            || world == null
            || camera == null
            || viewport == null
            || !player.isAlive()
            || !ItemRegistry.isPlaceableBlock(heldItemId)) {
            return;
        }

        camera.update();
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);

        int tileX = (int) Math.floor(mouseWorld.x);
        int tileY = (int) Math.floor(mouseWorld.y);
        if (isValidPlacement(player, world, tileX, tileY)) {
            hoveredPlaceX = tileX;
            hoveredPlaceY = tileY;
        }
    }

    private boolean isValidPlacement(Player player, World world, int x, int y) {
        if (!world.isInBounds(x, y)
            || world.getBlock(x, y) != null
            || overlapsPlayer(player, x, y)
            || !isWithinReach(player, x, y)) {
            return false;
        }
        return world.isSolid(x - 1, y)
            || world.isSolid(x + 1, y)
            || world.isSolid(x, y - 1)
            || world.isSolid(x, y + 1);
    }

    private boolean overlapsPlayer(Player player, int tileX, int tileY) {
        Rectangle tileBounds = new Rectangle(tileX, tileY, 1f, 1f);
        return tileBounds.overlaps(player.getBounds());
    }

    private boolean isWithinReach(Player player, int tileX, int tileY) {
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;
        float tileCenterX = tileX + 0.5f;
        float tileCenterY = tileY + 0.5f;
        float dx = tileCenterX - playerCenterX;
        float dy = tileCenterY - playerCenterY;
        return dx * dx + dy * dy <= PLACE_REACH * PLACE_REACH;
    }
}
