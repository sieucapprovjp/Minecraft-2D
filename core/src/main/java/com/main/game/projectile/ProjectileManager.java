package com.main.game.projectile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.world.World;
import java.util.Random;

public class ProjectileManager {

    private static final float PILLAGER_ARROW_WIDTH = 0.42f;
    private static final float PILLAGER_ARROW_HEIGHT = 0.12f;
    private static final float PILLAGER_ARROW_SPEED = 9f;
    private static final float PILLAGER_ARROW_LIFETIME = 3f;
    private static final float MAX_SPREAD_DEGREES = 4f;
    private static final float EVOKER_MAGIC_WIDTH = 0.32f;
    private static final float EVOKER_MAGIC_HEIGHT = 0.2f;
    private static final float EVOKER_MAGIC_SPEED = 8.5f;
    private static final float EVOKER_MAGIC_LIFETIME = 3f;

    private final Array<Projectile> projectiles = new Array<>();
    private final Random random;
    private ProjectileRenderer renderer;
    private ProjectileHitListener hitListener;

    public ProjectileManager() {
        this(new Random());
    }

    public ProjectileManager(Random random) {
        this.random = random == null ? new Random() : random;
    }

    public void spawnFromMobAttack(Mob shooter, Player target, int damage) {
        if (shooter == null || target == null || !shooter.isAlive() || !target.isAlive()) {
            return;
        }
        float spread = randomSpreadDegrees();
        Vector2 direction = ProjectileAim.directionToPlayer(shooter, target, spread);
        Vector2 center = ProjectileAim.spawnCenter(shooter, direction);
        projectiles.add(new Projectile(
            ProjectileType.PILLAGER_ARROW,
            center.x - PILLAGER_ARROW_WIDTH * 0.5f,
            center.y - PILLAGER_ARROW_HEIGHT * 0.5f,
            PILLAGER_ARROW_WIDTH,
            PILLAGER_ARROW_HEIGHT,
            direction.x * PILLAGER_ARROW_SPEED,
            direction.y * PILLAGER_ARROW_SPEED,
            damage,
            PILLAGER_ARROW_LIFETIME));
    }

    public void spawnEvokerMagic(Mob shooter, Player target, int damage) {
        if (shooter == null || target == null || !shooter.isAlive() || !target.isAlive()) {
            return;
        }
        float spread = randomSpreadDegrees() * 0.5f;
        Vector2 direction = ProjectileAim.directionToPlayer(shooter, target, spread);
        Vector2 center = ProjectileAim.spawnCenter(shooter, direction);
        projectiles.add(new Projectile(
            ProjectileType.EVOKER_MAGIC,
            center.x - EVOKER_MAGIC_WIDTH * 0.5f,
            center.y - EVOKER_MAGIC_HEIGHT * 0.5f,
            EVOKER_MAGIC_WIDTH,
            EVOKER_MAGIC_HEIGHT,
            direction.x * EVOKER_MAGIC_SPEED,
            direction.y * EVOKER_MAGIC_SPEED,
            damage,
            EVOKER_MAGIC_LIFETIME));
    }

    public void setHitListener(ProjectileHitListener hitListener) {
        this.hitListener = hitListener;
    }

    public void update(float delta, World world, Player player) {
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(delta, world);
            if (player != null && player.isAlive() && projectile.overlaps(player.getBounds())) {
                int healthBefore = player.getHealth();
                player.takeDamage(projectile.getDamage());
                if (player.getHealth() < healthBefore && hitListener != null) {
                    hitListener.onProjectileHitPlayer(projectile.getType());
                }
                projectile.kill();
            }
            if (!projectile.isAlive()) {
                projectiles.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (projectiles.size == 0) {
            return;
        }
        ProjectileRenderer projectileRenderer = getRenderer();
        for (Projectile projectile : projectiles) {
            projectileRenderer.render(batch, projectile);
        }
    }

    public void clear() {
        projectiles.clear();
    }

    public void dispose() {
        clear();
        if (renderer != null) {
            renderer.dispose();
            renderer = null;
        }
    }

    public int activeCount() {
        return projectiles.size;
    }

    Projectile get(int index) {
        return projectiles.get(index);
    }

    private float randomSpreadDegrees() {
        return -MAX_SPREAD_DEGREES + random.nextFloat() * MAX_SPREAD_DEGREES * 2f;
    }

    private ProjectileRenderer getRenderer() {
        if (renderer == null) {
            renderer = new ProjectileRenderer();
        }
        return renderer;
    }
}
