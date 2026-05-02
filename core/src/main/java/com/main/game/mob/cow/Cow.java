package com.main.game.mob.cow;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.entities.Entity;
import com.main.game.utils.Constants;

public class Cow extends Entity {

    private final Texture idleTexture;
    private final Texture hurtTexture;
    private final Texture[] walkTextures;
    private final float minX;
    private final float maxX;
    private final float baseY;
    private float animationTime;
    private float behaviorTimer;
    private float pauseTimer;

    public Cow(float x, float y, float minX, float maxX) {
        super(x, y, Constants.COW_WIDTH, Constants.COW_HEIGHT);
        this.idleTexture = new Texture(Constants.COW_IDLE_PATH);
        this.hurtTexture = new Texture(Constants.COW_HURT_PATH);
        this.walkTextures = new Texture[Constants.COW_WALK_PATHS.length];
        for (int i = 0; i < Constants.COW_WALK_PATHS.length; i++) {
            walkTextures[i] = new Texture(Constants.COW_WALK_PATHS[i]);
        }
        this.minX = minX;
        this.maxX = maxX;
        this.baseY = y;
        this.velocity.x = Constants.COW_SPEED * 0.75f;
        this.facingRight = true;
    }

    @Override
    public void update(float delta) {
        animationTime += delta;
        behaviorTimer += delta;

        if (pauseTimer > 0f) {
            pauseTimer -= delta;
            velocity.x = 0f;
        } else if (behaviorTimer >= 1.8f) {
            behaviorTimer = 0f;
            float decision = (float) Math.random();
            if (decision < 0.25f) {
                pauseTimer = 0.8f + (float) Math.random() * 1.2f;
                velocity.x = 0f;
            } else {
                float direction = decision < 0.6f ? -1f : 1f;
                float speedScale = 0.55f + (float) Math.random() * 0.65f;
                velocity.x = direction * Constants.COW_SPEED * speedScale;
                facingRight = direction > 0f;
            }
        }

        position.x += velocity.x * delta;
        position.y = baseY;
        updateBounds();

        if (position.x <= minX) {
            position.x = minX;
            velocity.x = Math.abs(Constants.COW_SPEED * (0.6f + (float) Math.random() * 0.4f));
            facingRight = true;
        } else if (position.x + width >= maxX) {
            position.x = maxX - width;
            velocity.x = -Math.abs(Constants.COW_SPEED * (0.6f + (float) Math.random() * 0.4f));
            facingRight = false;
        }
        updateBounds();
    }

    @Override
    public void render(SpriteBatch batch) {
        Texture frame = selectFrame();
        float drawWidth = facingRight ? width : -width;
        float drawX = facingRight ? position.x : position.x + width;
        batch.draw(frame, drawX, position.y, drawWidth, height);
    }

    private Texture selectFrame() {
        if (!isAlive) {
            return hurtTexture;
        }
        if (Math.abs(velocity.x) < 0.05f) {
            return idleTexture;
        }
        int frameIndex = ((int) (animationTime / 0.12f)) % walkTextures.length;
        return walkTextures[frameIndex];
    }

    @Override
    public void dispose() {
        idleTexture.dispose();
        hurtTexture.dispose();
        for (Texture walkTexture : walkTextures) {
            walkTexture.dispose();
        }
    }
}
