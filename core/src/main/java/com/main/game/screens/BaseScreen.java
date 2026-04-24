package com.main.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.MainGame;
import com.main.game.utils.Constants;

/**
 * Abstract base cho tất cả Screen trong game.
 * Mọi screen (GameScreen, MenuScreen, ...) đều extend class này.
 *
 * Đã setup sẵn:
 *  - SpriteBatch dùng chung từ MainGame
 *  - OrthographicCamera với đơn vị tile
 *  - FitViewport giữ tỉ lệ màn hình
 *
 * TODO(HUY-LEAD):
 *  - Quy định rõ thứ tự update/render chuẩn cho mọi screen.
 *  - Bổ sung utility chung cho debug overlay hoặc UI layer nếu cần.
 */
public abstract class BaseScreen implements Screen {

    protected final MainGame game;
    protected final SpriteBatch batch;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;

    public BaseScreen(MainGame game) {
        this.game     = game;
        this.batch    = game.getBatch();
        this.camera   = new OrthographicCamera();
        this.viewport = new FitViewport(
            Constants.VIEWPORT_WIDTH_TILES,
            Constants.VIEWPORT_HEIGHT_TILES,
            camera
        );
        // Camera bắt đầu ở góc dưới-trái của world
        camera.position.set(
            Constants.VIEWPORT_WIDTH_TILES  / 2f,
            Constants.VIEWPORT_HEIGHT_TILES / 2f,
            0
        );
        camera.update();
    }

    // ─── Abstract — mỗi screen tự implement ──────────────────────

    /** Logic update, gọi mỗi frame trước khi render */
    public abstract void update(float delta);

    /** Vẽ toàn bộ nội dung screen */
    public abstract void draw();

    /** Định danh screen cho ScreenRouter */
    public abstract com.main.game.navigation.ScreenId getScreenId();

    // ─── Screen lifecycle ─────────────────────────────────────────

    /** Hook khi screen vừa được kích hoạt */
    public void onEnter() {}

    /** Hook khi screen chuẩn bị rời khỏi stack */
    public void onExit() {}

    @Override
    public final void render(float delta) {
        update(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show()   {}

    @Override
    public void hide()   {}

    @Override
    public void pause()  {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}
