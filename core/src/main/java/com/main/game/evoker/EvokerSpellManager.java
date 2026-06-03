package com.main.game.evoker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.projectile.ProjectileManager;
import com.main.game.world.World;

public final class EvokerSpellManager {

    private static final float FANG_MAX_HORIZONTAL_DISTANCE = 5.5f;
    private static final float FANG_MAX_VERTICAL_DELTA = 2.0f;

    public enum CastResult {
        NONE,
        FANGS,
        PROJECTILE
    }

    private final EvokerFangManager fangManager = new EvokerFangManager();

    public void setFangHitListener(Runnable hitListener) {
        fangManager.setHitListener(hitListener);
    }

    public CastResult cast(Mob caster, Player target, int projectileDamage, World world,
                           ProjectileManager projectileManager) {
        if (caster == null || target == null || !caster.isAlive() || !target.isAlive()) {
            return CastResult.NONE;
        }
        if (shouldCastFangs(caster, target)) {
            fangManager.spawnLine(caster, target, world);
            return CastResult.FANGS;
        } else if (projectileManager != null) {
            projectileManager.spawnEvokerMagic(caster, target, projectileDamage);
            return CastResult.PROJECTILE;
        }
        return CastResult.NONE;
    }

    public void update(float delta, World world, Player player) {
        fangManager.update(delta, world, player);
    }

    public void render(SpriteBatch batch) {
        fangManager.render(batch);
    }

    public void dispose() {
        fangManager.dispose();
    }

    public int activeFangCount() {
        return fangManager.activeCount();
    }

    static boolean shouldCastFangs(Mob caster, Player target) {
        float casterCenterX = caster.getX() + caster.getWidth() * 0.5f;
        float targetCenterX = target.getX() + target.getWidth() * 0.5f;
        float horizontalDistance = Math.abs(targetCenterX - casterCenterX);
        float verticalDelta = Math.abs(target.getY() - caster.getY());
        return horizontalDistance <= FANG_MAX_HORIZONTAL_DISTANCE
            && verticalDelta <= FANG_MAX_VERTICAL_DELTA;
    }
}
