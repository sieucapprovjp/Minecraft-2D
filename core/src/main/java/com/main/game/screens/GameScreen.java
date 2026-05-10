package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.main.game.MainGame;
import com.main.game.entities.player.Player;
import com.main.game.entities.player.Player.Appearance;
import com.main.game.mob.cow.Cow;
import com.main.game.navigation.ScreenId;
import com.main.game.physics.PhysicsEngine;
import com.main.game.utils.Constants;
import com.main.game.world.BlockPalette;
import com.main.game.world.World;

public class GameScreen extends BaseScreen {

    private World world;
    private PhysicsEngine physics;
    private Player player;
    private Cow cow;

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        disposeEntities();

        world = new World();
        world.generate(1337L);
        physics = new PhysicsEngine();

        int playerSpawnX = 16;
        int cowSpawnX = 22;

        player = new Player(playerSpawnX, world.getSurfaceY(playerSpawnX) + 1f);
        cow = new Cow(cowSpawnX, world.getSurfaceY(cowSpawnX) + 1f, cowSpawnX - 4f, cowSpawnX + 4f);

        camera.position.set(player.getX(), player.getY() + 5f, 0f);
        camera.update();
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            show();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            player.setAppearance(Appearance.BODY_2);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            player.setAppearance(Appearance.BODY_4);
        }

        player.update(delta);
        physics.update(player, world, delta);

        cow.update(delta);

        camera.position.x = player.getX();
        camera.position.y = player.getY() + 5f;

        float halfW = camera.viewportWidth / 2f;
        float halfH = camera.viewportHeight / 2f;
        camera.position.x = Math.max(halfW, Math.min(world.width - halfW, camera.position.x));
        camera.position.y = Math.max(halfH, Math.min(world.height - halfH, camera.position.y));
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0.4f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        cow.render(batch);
        player.render(batch);
        batch.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(BlockPalette.GRASS, 0.25f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        batch.draw(BlockPalette.STONE, 1.35f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        batch.draw(BlockPalette.BEDROCK, 2.45f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        disposeEntities();
        BlockPalette.dispose();
    }

    private void disposeEntities() {
        if (player != null) {
            player.dispose();
            player = null;
        }
        if (cow != null) {
            cow.dispose();
            cow = null;
        }
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.GAME;
    }
}
