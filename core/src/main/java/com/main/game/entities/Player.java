package com.main.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.utils.Constants;

public class Player extends Entity {

    public enum PlayerState {
        IDLE,
        RUN,
        JUMP,
        FALL
    }

    private final Texture idleTexture;
    private final Texture walkATexture;
    private final Texture walkBTexture;
    private final Texture jumpTexture;
    private final Texture headRightTexture;
    private final Texture headLeftTexture;
    private final Texture armIdleTexture;
    private final Texture armWalkTexture;
    private final Texture legsIdleTexture;
    private final Texture legsWalkTexture;
    private final Texture bootsIdleTexture;
    private final Texture bootsWalkTexture;

    private PlayerState state;
    private float walkTimer;

    public Player(float x, float y) {
        super(x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        idleTexture = new Texture("mvp/player/idle.png");
        walkATexture = new Texture("mvp/player/walk_1.png");
        walkBTexture = new Texture("mvp/player/walk_2.png");
        jumpTexture = new Texture("mvp/player/jump.png");
        headRightTexture = new Texture("mvp/player/steve_head_simple_right.png");
        headLeftTexture = new Texture("mvp/player/steve_head_simple_left.png");
        armIdleTexture = new Texture("mvp/player/steve_arm_idle.png");
        armWalkTexture = new Texture("mvp/player/steve_arm_walk_01.png");
        legsIdleTexture = new Texture("mvp/player/steve_legs_idle.png");
        legsWalkTexture = new Texture("mvp/player/steve_legs_walk_01.png");
        bootsIdleTexture = new Texture("mvp/player/steve_boots_idle.png");
        bootsWalkTexture = new Texture("mvp/player/steve_boots_walk_01.png");
        state = PlayerState.IDLE;
        walkTimer = 0f;
    }

    @Override
    public void update(float delta) {
        float horizontal = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontal -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontal += 1f;
        }

        velocity.x = horizontal * Constants.PLAYER_SPEED;
        if (horizontal < 0f) {
            facingRight = false;
        } else if (horizontal > 0f) {
            facingRight = true;
        }

        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && onGround) {
            velocity.y = Constants.PLAYER_JUMP_FORCE;
            onGround = false;
        }

        if (!onGround) {
            if (velocity.y > 0f) {
                state = PlayerState.JUMP;
            } else {
                state = PlayerState.FALL;
            }
        } else if (Math.abs(velocity.x) > 0.001f) {
            state = PlayerState.RUN;
        } else {
            state = PlayerState.IDLE;
        }

        if (state == PlayerState.RUN) {
            walkTimer += delta;
        } else {
            walkTimer = 0f;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Texture bodyFrame = selectBodyFrame();
        Texture armFrame = (state == PlayerState.RUN) ? armWalkTexture : armIdleTexture;
        Texture legsFrame = (state == PlayerState.RUN) ? legsWalkTexture : legsIdleTexture;
        Texture bootsFrame = (state == PlayerState.RUN) ? bootsWalkTexture : bootsIdleTexture;
        Texture headFrame = facingRight ? headRightTexture : headLeftTexture;

        float baseX = position.x;
        float baseY = position.y;

        // Layer order: boots -> legs -> body -> head -> arm (arm ngoài cùng)
        drawPart(batch, bootsFrame, baseX + 0.12f, baseY + 0.00f, 0.66f, 0.42f);
        drawPart(batch, legsFrame,  baseX + 0.12f, baseY + 0.10f, 0.66f, 0.75f);
        drawPart(batch, bodyFrame,  baseX + 0.12f, baseY + 0.45f, 0.66f, 0.85f);
        drawPart(batch, headFrame,  baseX + 0.05f, baseY + 1.16f, 0.82f, 0.74f);
        drawPart(batch, armFrame,   baseX + 0.58f, baseY + 0.46f, 0.34f, 0.70f);
    }

    private void drawPart(SpriteBatch batch, Texture texture, float x, float y, float w, float h) {
        if (facingRight) {
            batch.draw(texture, x, y, w, h);
        } else {
            batch.draw(texture, x + w, y, -w, h);
        }
    }

    private Texture selectBodyFrame() {
        if (state == PlayerState.RUN) {
            int frameIndex = ((int) (walkTimer / 0.15f)) % 2;
            return frameIndex == 0 ? walkATexture : walkBTexture;
        }
        if (state == PlayerState.JUMP || state == PlayerState.FALL) {
            return jumpTexture;
        }
        return idleTexture;
    }

    public PlayerState getState() {
        return state;
    }

    @Override
    public void dispose() {
        idleTexture.dispose();
        walkATexture.dispose();
        walkBTexture.dispose();
        jumpTexture.dispose();
        headRightTexture.dispose();
        headLeftTexture.dispose();
        armIdleTexture.dispose();
        armWalkTexture.dispose();
        legsIdleTexture.dispose();
        legsWalkTexture.dispose();
        bootsIdleTexture.dispose();
        bootsWalkTexture.dispose();
    }
}
