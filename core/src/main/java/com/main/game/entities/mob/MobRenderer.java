package com.main.game.entities.mob;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

final class MobRenderer {

    private static final float PIXELS_PER_TILE = 80f;

    void render(SpriteBatch batch, Mob mob, Animation<TextureRegion> idleAnim,
                Animation<TextureRegion> walkAnim, Animation<TextureRegion> hurtAnim) {
        if (batch == null || mob == null || !mob.isAlive()) {
            return;
        }

        TextureRegion frame = getCurrentFrame(mob, idleAnim, walkAnim, hurtAnim);
        if (frame == null) {
            return;
        }

        boolean needFlip = (!mob.isFacingRight() && !frame.isFlipX())
            || (mob.isFacingRight() && frame.isFlipX());
        if (needFlip) {
            frame.flip(true, false);
        }

        float renderWidth = renderWidth(frame, mob.getWidth(), mob.getRenderPixelsPerTile());
        float renderHeight = renderHeight(frame, mob.getHeight(), mob.getRenderPixelsPerTile());
        float capScale = constrainedRenderScale(renderWidth, renderHeight,
            mob.getMaxRenderWidth(), mob.getMaxRenderHeight());
        renderWidth *= capScale;
        renderHeight *= capScale;
        batch.draw(frame,
            centeredRenderX(mob.getX(), mob.getWidth(), renderWidth),
            mob.getY(),
            renderWidth,
            renderHeight);
    }

    private TextureRegion getCurrentFrame(Mob mob, Animation<TextureRegion> idleAnim,
                                          Animation<TextureRegion> walkAnim, Animation<TextureRegion> hurtAnim) {
        switch (mob.getState()) {
            case RUN:
                return walkAnim != null ? walkAnim.getKeyFrame(mob.getStateTime()) : null;
            case JUMP:
            case FALL:
                return walkAnim != null ? walkAnim.getKeyFrame(0f) : null;
            case HURT:
                return hurtAnim != null ? hurtAnim.getKeyFrame(mob.getStateTime()) : null;
            case IDLE:
            default:
                return idleAnim != null ? idleAnim.getKeyFrame(mob.getStateTime()) : null;
        }
    }

    static float renderWidth(TextureRegion frame, float fallbackWidth) {
        return renderWidth(frame, fallbackWidth, PIXELS_PER_TILE);
    }

    static float renderWidth(TextureRegion frame, float fallbackWidth, float pixelsPerTile) {
        return widthTilesForPixels(frame == null ? 0 : frame.getRegionWidth(), fallbackWidth, pixelsPerTile);
    }

    static float renderHeight(TextureRegion frame, float fallbackHeight) {
        return renderHeight(frame, fallbackHeight, PIXELS_PER_TILE);
    }

    static float renderHeight(TextureRegion frame, float fallbackHeight, float pixelsPerTile) {
        return heightTilesForPixels(frame == null ? 0 : frame.getRegionHeight(), fallbackHeight, pixelsPerTile);
    }

    static float widthTilesForPixels(int regionWidth, float fallbackWidth) {
        return widthTilesForPixels(regionWidth, fallbackWidth, PIXELS_PER_TILE);
    }

    static float widthTilesForPixels(int regionWidth, float fallbackWidth, float pixelsPerTile) {
        return regionWidth > 0 ? regionWidth / validPixelsPerTile(pixelsPerTile) : fallbackWidth;
    }

    static float heightTilesForPixels(int regionHeight, float fallbackHeight) {
        return heightTilesForPixels(regionHeight, fallbackHeight, PIXELS_PER_TILE);
    }

    static float heightTilesForPixels(int regionHeight, float fallbackHeight, float pixelsPerTile) {
        return regionHeight > 0 ? regionHeight / validPixelsPerTile(pixelsPerTile) : fallbackHeight;
    }

    static float centeredRenderX(float mobX, float collisionWidth, float renderWidth) {
        return mobX + (collisionWidth - renderWidth) * 0.5f;
    }

    static float constrainedRenderScale(float renderWidth, float renderHeight,
                                        float maxRenderWidth, float maxRenderHeight) {
        if (renderWidth <= 0f || renderHeight <= 0f) {
            return 1f;
        }
        float scale = 1f;
        if (maxRenderWidth > 0f && renderWidth > maxRenderWidth) {
            scale = Math.min(scale, maxRenderWidth / renderWidth);
        }
        if (maxRenderHeight > 0f && renderHeight > maxRenderHeight) {
            scale = Math.min(scale, maxRenderHeight / renderHeight);
        }
        return scale;
    }

    private static float validPixelsPerTile(float pixelsPerTile) {
        return pixelsPerTile > 0f ? pixelsPerTile : PIXELS_PER_TILE;
    }
}
