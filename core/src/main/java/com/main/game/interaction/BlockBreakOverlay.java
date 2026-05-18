package com.main.game.interaction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BlockBreakOverlay {

    private final Texture cursorTexture;
    private final Texture[] digTextures;

    public BlockBreakOverlay() {
        cursorTexture = new Texture(Gdx.files.internal("cursor/cursor.png"));
        digTextures = new Texture[10];
        for (int i = 0; i < digTextures.length; i++) {
            digTextures[i] = new Texture(Gdx.files.internal("cursor/dig" + (i + 1) + ".png"));
        }
    }

    public void render(SpriteBatch batch, BlockBreaker blockBreaker) {
        if (!blockBreaker.hasHoveredBlock()) {
            return;
        }

        int tileX = blockBreaker.getHoveredBlockX();
        int tileY = blockBreaker.getHoveredBlockY();
        batch.setColor(Color.WHITE);
        batch.draw(cursorTexture, tileX, tileY, 1f, 1f);

        int stage = blockBreaker.getCrackStageIndex(digTextures.length);
        if (stage >= 0) {
            batch.draw(digTextures[stage], tileX, tileY, 1f, 1f);
        }
    }

    public void dispose() {
        cursorTexture.dispose();
        for (Texture texture : digTextures) {
            texture.dispose();
        }
    }
}
