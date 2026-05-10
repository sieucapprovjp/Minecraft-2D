package com.main.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.main.game.entities.Entity;
import com.main.game.utils.Constants;

public class Player extends Entity {

    public enum Appearance {
        BODY_2,
        BODY_4
    }

    private final Texture body2Texture;
    private final Texture body4Texture;
    private final TextureRegion body2Region;
    private final TextureRegion body4Region;
    private Appearance appearance;

    // Animation
    private Animation<TextureRegion> walkAnimation;
    private float stateTime = 0f;
    private boolean isWalking = false;
    private Array<Texture> walkTextures;

    public Player(float x, float y) {
        super(x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        this.body2Texture = new Texture(Constants.PLAYER_BODY_1_PATH);
        this.body4Texture = new Texture(Constants.PLAYER_BODY_2_PATH);
        this.body2Region = new TextureRegion(body2Texture);
        this.body4Region = new TextureRegion(body4Texture);
        this.appearance = Appearance.BODY_2;

        loadWalkAnimation();
    }

    private void loadWalkAnimation() {
        walkTextures = new Array<>();
        Array<TextureRegion> frames = new Array<>();
        for (String path : Constants.PLAYER_WALK_PATHS) {
            Texture tex = new Texture(path);
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            walkTextures.add(tex);
            frames.add(new TextureRegion(tex));
        }
        // 0.05f duration per frame is a common default for walk cycles
        walkAnimation = new Animation<>(0.05f, frames, Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float delta) {
        float horizontal = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontal -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horizontal += 1f;
        }

        velocity.x = horizontal * Constants.PLAYER_SPEED;
        
        isWalking = (horizontal != 0f);
        if (isWalking) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        if (horizontal < 0f) {
            facingRight = false;
        } else if (horizontal > 0f) {
            facingRight = true;
        }

        boolean jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.W)
            || Gdx.input.isKeyJustPressed(Input.Keys.UP);
        if (jumpPressed && onGround) {
            velocity.y = Constants.PLAYER_JUMP_FORCE;
            onGround = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = selectFrame();
        float drawWidth = facingRight ? width : -width;
        float drawX = facingRight ? position.x : position.x + width;
        batch.draw(frame, drawX, position.y, drawWidth, height);
    }

    private TextureRegion selectFrame() {
        if (isWalking) {
            return walkAnimation.getKeyFrame(stateTime);
        } else {
            return appearance == Appearance.BODY_4 ? body4Region : body2Region;
        }
    }

    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    @Override
    public void dispose() {
        body2Texture.dispose();
        body4Texture.dispose();
        if (walkTextures != null) {
            for (Texture tex : walkTextures) {
                tex.dispose();
            }
        }
    }
}
