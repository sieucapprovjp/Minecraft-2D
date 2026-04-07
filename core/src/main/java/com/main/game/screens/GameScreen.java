package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.main.game.MainGame;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;

/**
 * Screen chính của game — nơi mọi module hội tụ.
 *
 * Mỗi thành viên chỉ cần quan tâm đến object của mình,
 * GameScreen lo việc gọi update/render theo đúng thứ tự.
 */
public class GameScreen extends BaseScreen {

    private World         world;         // Kiên
    private PhysicsEngine physics;       // Lâm Hùng
    // private Player     player;        // Đước thêm vào sau
    // private MobManager mobManager;    // Đước thêm vào sau

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        world   = new World();
        physics = new PhysicsEngine(world);
        // player = new Player(...);     // Đước khởi tạo ở đây
    }

    @Override
    public void render(float delta) {
        // 1. Clear màn hình
        Gdx.gl.glClearColor(0.4f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2. Update logic
        // physics.update(player, delta);   // Lâm Hùng / Đước
        // mobManager.update(delta);        // Đước
        // camera theo player               // Kiên

        // 3. Render
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);        // Kiên
        // player.render(batch);            // Đước
        // mobManager.render(batch);        // Đước
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        // player.dispose();
        // mobManager.dispose();
    }
}
