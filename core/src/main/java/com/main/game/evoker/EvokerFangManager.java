package com.main.game.evoker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.world.World;

public final class EvokerFangManager {

    private static final int FANG_COUNT = 5;
    private static final float FANG_SPACING_TILES = 1.0f;
    private static final float FANG_DELAY_SECONDS = 2f / 60f;
    private static final int FANG_DAMAGE = 6;

    private final Array<EvokerFang> fangs = new Array<>();
    private EvokerFangRenderer renderer;
    private Runnable hitListener;

    public void spawnLine(Mob caster, Player target, World world) {
        if (caster == null || target == null || !caster.isAlive() || !target.isAlive()) {
            return;
        }
        float casterCenterX = caster.getX() + caster.getWidth() * 0.5f;
        float targetCenterX = target.getX() + target.getWidth() * 0.5f;
        spawnLine(casterCenterX, caster.getY(), targetCenterX, world);
    }

    void spawnLine(float casterCenterX, float casterY, float targetCenterX, World world) {
        float direction = targetCenterX >= casterCenterX ? 1f : -1f;
        for (int i = 0; i < FANG_COUNT; i++) {
            float centerX = casterCenterX + direction * (i + 1) * FANG_SPACING_TILES;
            float y = findGroundTopY(world, centerX, casterY);
            fangs.add(new EvokerFang(centerX, y, i * FANG_DELAY_SECONDS, FANG_DAMAGE));
        }
    }

    public void update(float delta, World world, Player player) {
        for (int i = fangs.size - 1; i >= 0; i--) {
            EvokerFang fang = fangs.get(i);
            boolean damagedPlayer = fang.update(delta, player);
            if (damagedPlayer && hitListener != null) {
                hitListener.run();
            }
            if (!fang.isAlive()) {
                fangs.removeIndex(i);
            }
        }
    }

    public void setHitListener(Runnable hitListener) {
        this.hitListener = hitListener;
    }

    public void render(SpriteBatch batch) {
        if (fangs.size == 0) {
            return;
        }
        EvokerFangRenderer fangRenderer = getRenderer();
        for (EvokerFang fang : fangs) {
            fangRenderer.render(batch, fang);
        }
    }

    public void clear() {
        fangs.clear();
    }

    public void dispose() {
        clear();
        if (renderer != null) {
            renderer.dispose();
            renderer = null;
        }
    }

    public int activeCount() {
        return fangs.size;
    }

    EvokerFang get(int index) {
        return fangs.get(index);
    }

    private float findGroundTopY(World world, float centerX, float fallbackY) {
        if (world == null) {
            return fallbackY;
        }
        int tileX = (int) Math.floor(centerX);
        if (tileX < 0 || tileX >= world.width) {
            return fallbackY;
        }
        int surfaceY = world.getSurfaceY(tileX);
        if (surfaceY >= 0) {
            return surfaceY + 1f;
        }
        return fallbackY;
    }

    private EvokerFangRenderer getRenderer() {
        if (renderer == null) {
            renderer = new EvokerFangRenderer();
        }
        return renderer;
    }
}
