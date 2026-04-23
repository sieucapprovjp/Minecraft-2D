package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;
import com.main.game.MainGame;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;

/**
 * Screen chính của game — nơi mọi module hội tụ.
 *
 * Mỗi thành viên chỉ cần quan tâm đến object của mình,
 * GameScreen lo việc gọi update/render theo đúng thứ tự.
 *
 * TODO(HUY-LEAD):
 *  - Đây là điểm integration chính, chỉ merge khi module giao diện giữa các team ổn định.
 */
public class GameScreen extends BaseScreen {

    private World         world;         // TODO(KIEN-WORLD): quản lý world/chunk/camera follow
    private PhysicsEngine physics;       // TODO(LHUNG-PHYSICS): collision + resolve
    // private Player     player;        // TODO(DUOC-ENTITY): player input/state
    // private MobManager mobManager;    // TODO(DUOC-ENTITY): AI mob update/render

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        world   = new World();
        // TODO(KIEN-WORLD): seed nên lấy từ save/game config thay vì hardcode.
        world.generate(1337L);
        physics = new PhysicsEngine();
        // TODO(DUOC-ENTITY): khởi tạo Player + MobManager tại đây.
    }

    @Override
    public void update(float delta) {
        // TODO(KIEN-WORLD): camera test WASD chỉ dùng tạm; thay bằng camera follow player.
        float cameraSpeed = 16f; // tile/s

        if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.position.x -= cameraSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.position.x += cameraSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.position.y -= cameraSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.position.y += cameraSpeed * delta;

        float halfW = camera.viewportWidth / 2f;
        float halfH = camera.viewportHeight / 2f;
        camera.position.x = Math.max(halfW, Math.min(world.width - halfW, camera.position.x));
        camera.position.y = Math.max(halfH, Math.min(world.height - halfH, camera.position.y));

        // TODO(LHUNG-PHYSICS + DUOC-ENTITY):
        //  - physics.update(player, delta)
        //  - collision world/entity
        //  - mobManager.update(delta)
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0.4f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        // TODO(DUOC-ENTITY): render player.
        // TODO(DUOC-ENTITY): render mob manager.
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        // player.dispose();
        // mobManager.dispose();
    }
}
