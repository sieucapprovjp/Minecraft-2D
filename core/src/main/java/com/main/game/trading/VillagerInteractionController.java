package com.main.game.trading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;

public final class VillagerInteractionController {

    private static final float OPEN_REACH = 3.2f;

    private final Vector2 mouseWorld = new Vector2();
    private Mob hoveredVillager;

    public boolean canOpen(Player player, EntityManager entityManager,
                           OrthographicCamera camera, Viewport viewport) {
        hoveredVillager = null;
        if (player == null
            || entityManager == null
            || camera == null
            || viewport == null
            || !player.isAlive()) {
            return false;
        }

        camera.update();
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);

        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;
        Mob nearest = null;
        float nearestDistance2 = Float.MAX_VALUE;
        for (Mob mob : entityManager.getMobs()) {
            if (mob == null
                || mob.getType() != Mob.MobType.VILLAGER
                || !mob.isAlive()
                || !mob.getBounds().contains(mouseWorld)
                || !isWithinReach(mob, playerCenterX, playerCenterY)) {
                continue;
            }

            float dx = (mob.getX() + mob.getWidth() / 2f) - playerCenterX;
            float dy = (mob.getY() + mob.getHeight() / 2f) - playerCenterY;
            float distance2 = dx * dx + dy * dy;
            if (distance2 < nearestDistance2) {
                nearestDistance2 = distance2;
                nearest = mob;
            }
        }
        hoveredVillager = nearest;
        return hoveredVillager != null;
    }

    public Mob getHoveredVillager() {
        return hoveredVillager;
    }

    private boolean isWithinReach(Mob mob, float playerCenterX, float playerCenterY) {
        Rectangle bounds = mob.getBounds();
        float closestX = Math.max(bounds.x, Math.min(playerCenterX, bounds.x + bounds.width));
        float closestY = Math.max(bounds.y, Math.min(playerCenterY, bounds.y + bounds.height));
        float dx = closestX - playerCenterX;
        float dy = closestY - playerCenterY;
        return dx * dx + dy * dy <= OPEN_REACH * OPEN_REACH;
    }
}
