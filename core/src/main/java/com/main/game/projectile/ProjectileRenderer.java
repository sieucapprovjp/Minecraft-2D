package com.main.game.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

final class ProjectileRenderer {

    private static final Color ARROW_COLOR = new Color(0.18f, 0.16f, 0.13f, 1f);
    private static final Color EVOKER_MAGIC_COLOR = new Color(0.45f, 0.25f, 0.75f, 1f);

    private final Texture pixel;

    ProjectileRenderer() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
    }

    void render(SpriteBatch batch, Projectile projectile) {
        if (batch == null || projectile == null || !projectile.isAlive()) {
            return;
        }

        Color color = batch.getColor();
        float previousR = color.r;
        float previousG = color.g;
        float previousB = color.b;
        float previousA = color.a;

        batch.setColor(colorFor(projectile.getType()));
        batch.draw(pixel,
            projectile.getX(),
            projectile.getY(),
            projectile.getWidth() * 0.5f,
            projectile.getHeight() * 0.5f,
            projectile.getWidth(),
            projectile.getHeight(),
            1f,
            1f,
            projectile.getRotationDegrees(),
            0,
            0,
            1,
            1,
            false,
            false);
        batch.setColor(previousR, previousG, previousB, previousA);
    }

    void dispose() {
        pixel.dispose();
    }

    private Color colorFor(ProjectileType type) {
        return type == ProjectileType.EVOKER_MAGIC ? EVOKER_MAGIC_COLOR : ARROW_COLOR;
    }
}
