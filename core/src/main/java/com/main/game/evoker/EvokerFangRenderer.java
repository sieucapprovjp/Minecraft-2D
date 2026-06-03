package com.main.game.evoker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

final class EvokerFangRenderer {

    private static final float PIXELS_PER_TILE = 80f;

    private Texture closedTexture;
    private Texture openTexture;
    private TextureRegion closedRegion;
    private TextureRegion openRegion;

    void render(SpriteBatch batch, EvokerFang fang) {
        if (batch == null || fang == null || !fang.isVisible()) {
            return;
        }
        ensureLoaded();
        TextureRegion region = fang.isOpen() ? openRegion : closedRegion;
        float scale = fang.getScale();
        float width = region.getRegionWidth() / PIXELS_PER_TILE * scale;
        float height = region.getRegionHeight() / PIXELS_PER_TILE * scale;
        batch.draw(region, fang.getCenterX() - width * 0.5f, fang.getY(), width, height);
    }

    void dispose() {
        if (closedTexture != null) {
            closedTexture.dispose();
            closedTexture = null;
        }
        if (openTexture != null) {
            openTexture.dispose();
            openTexture = null;
        }
        closedRegion = null;
        openRegion = null;
    }

    private void ensureLoaded() {
        if (closedTexture != null && openTexture != null) {
            return;
        }
        closedTexture = new Texture(Gdx.files.internal("mobs/evoker/mobs/evoker_fang_closed.png"));
        openTexture = new Texture(Gdx.files.internal("mobs/evoker/mobs/evoker_fang_open.png"));
        closedRegion = new TextureRegion(closedTexture);
        openRegion = new TextureRegion(openTexture);
    }
}
