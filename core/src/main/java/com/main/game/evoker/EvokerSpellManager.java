package com.main.game.evoker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.projectile.ProjectileManager;
import com.main.game.world.World;
import java.util.Random;

public final class EvokerSpellManager {

    private static final float FANG_MAX_HORIZONTAL_DISTANCE = 5.5f;
    private static final float FANG_MAX_VERTICAL_DELTA = 2.0f;

    public enum CastResult {
        NONE,
        SUMMON_VEX,
        FANGS,
        PROJECTILE
    }

    private final EvokerFangManager fangManager = new EvokerFangManager();
    private final Random random;
    private VexSummonListener vexSummonListener;

    public EvokerSpellManager() {
        this(new Random());
    }

    public EvokerSpellManager(Random random) {
        this.random = random == null ? new Random() : random;
    }

    public void setFangHitListener(Runnable hitListener) {
        fangManager.setHitListener(hitListener);
    }

    public void setVexSummonListener(VexSummonListener vexSummonListener) {
        this.vexSummonListener = vexSummonListener;
    }

    public CastResult cast(Mob caster, Player target, int projectileDamage, World world,
                           ProjectileManager projectileManager) {
        if (caster == null || target == null || !caster.isAlive() || !target.isAlive()) {
            return CastResult.NONE;
        }

        boolean canSummonVex = canSummonVex(caster, target);
        boolean canCastFangs = shouldCastFangs(caster, target);
        boolean canCastProjectile = projectileManager != null;
        CastResult spell = chooseSpell(canSummonVex, canCastFangs, canCastProjectile);
        switch (spell) {
            case SUMMON_VEX:
                return vexSummonListener.onSummonVex(caster, target)
                    ? CastResult.SUMMON_VEX
                    : CastResult.NONE;
            case FANGS:
                fangManager.spawnLine(caster, target, world);
                return CastResult.FANGS;
            case PROJECTILE:
                projectileManager.spawnEvokerMagic(caster, target, projectileDamage);
                return CastResult.PROJECTILE;
            case NONE:
            default:
                return CastResult.NONE;
        }
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

    private boolean canSummonVex(Mob caster, Player target) {
        return vexSummonListener != null && vexSummonListener.canSummonVex(caster, target);
    }

    CastResult chooseSpell(boolean canSummonVex, boolean canCastFangs, boolean canCastProjectile) {
        int availableSpellCount = (canSummonVex ? 1 : 0)
            + (canCastFangs ? 1 : 0)
            + (canCastProjectile ? 1 : 0);
        if (availableSpellCount == 0) {
            return CastResult.NONE;
        }

        int choice = random.nextInt(availableSpellCount);
        if (canSummonVex && choice-- == 0) {
            return CastResult.SUMMON_VEX;
        }
        if (canCastFangs && choice-- == 0) {
            return CastResult.FANGS;
        }
        if (canCastProjectile) {
            return CastResult.PROJECTILE;
        }
        return CastResult.NONE;
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
