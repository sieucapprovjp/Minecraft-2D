package com.main.game.entities.mob;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public final class MobHitboxDebugRenderer {

    private static final float LINE_THICKNESS = 0.035f;
    private static final Color HITBOX_COLOR = new Color(1f, 0f, 0f, 0.9f);

    private final Texture pixel;

    public MobHitboxDebugRenderer() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
    }

    public void render(SpriteBatch batch, Mob mob) {
        if (batch == null || mob == null || !mob.isAlive()) {
            return;
        }

        Rectangle bounds = mob.getBounds();
        if (bounds.width <= 0f || bounds.height <= 0f) {
            return;
        }

        Color currentColor = batch.getColor();
        float previousR = currentColor.r;
        float previousG = currentColor.g;
        float previousB = currentColor.b;
        float previousA = currentColor.a;

        batch.setColor(HITBOX_COLOR);
        drawOutline(batch, bounds);
        batch.setColor(previousR, previousG, previousB, previousA);
    }

    public void dispose() {
        pixel.dispose();
    }

    private void drawOutline(SpriteBatch batch, Rectangle bounds) {
        float thickness = Math.min(LINE_THICKNESS, Math.min(bounds.width, bounds.height) * 0.5f);
        batch.draw(pixel, bounds.x, bounds.y, bounds.width, thickness);
        batch.draw(pixel, bounds.x, bounds.y + bounds.height - thickness, bounds.width, thickness);
        batch.draw(pixel, bounds.x, bounds.y, thickness, bounds.height);
        batch.draw(pixel, bounds.x + bounds.width - thickness, bounds.y, thickness, bounds.height);
    }
}
