package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.main.game.MainGame;
import com.main.game.entities.EntityManager;
import com.main.game.entities.Mob;
import com.main.game.entities.Player;
import com.main.game.navigation.ScreenId;
import com.main.game.physics.PhysicsEngine;
import com.main.game.utils.Constants;
import com.main.game.world.BlockPalette;
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

    private static final float CAMERA_ZOOM = 0.65f;

    private World         world;          // TODO(KIEN-WORLD): quản lý world/chunk/camera follow
    private PhysicsEngine physics;        // TODO(LHUNG-PHYSICS): collision + resolve
    private Player        player;         // DUOC-ENTITY: player input/state machine
    private EntityManager entityManager;  // DUOC-ENTITY: quản lý update/render entity
    private boolean paused;
    private Texture overlayTexture;
    private BitmapFont overlayFont;
    private GlyphLayout overlayLayout;
    private Matrix4 uiProjection;

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        world = new World();
        // TODO(KIEN-WORLD): seed nên lấy từ save/game config thay vì hardcode.
        world.generate(1337L);

        physics = new PhysicsEngine();

        // ── Khởi tạo Player ─────────────────────────── DUOC-ENTITY ──
        float spawnX = world.width * 0.25f;
        float spawnY = world.height * 0.58f;
        player = new Player(spawnX, spawnY, physics);

        // ── Khởi tạo EntityManager ───────────────────── DUOC-ENTITY ──
        entityManager = new EntityManager();
        entityManager.setPlayer(player);

        // ── Spawn mob mẫu để test ────────────────────── DUOC-ENTITY ──
        entityManager.addMob(new Mob(spawnX + 10f, spawnY, Mob.MobType.ZOMBIE,    player, physics));
        entityManager.addMob(new Mob(spawnX + 20f, spawnY, Mob.MobType.SKELETON,  player, physics));

        paused = false;
        camera.zoom = CAMERA_ZOOM;

        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(Color.WHITE);
        overlayPixmap.fill();
        overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();

        overlayFont = new BitmapFont();
        overlayFont.setColor(Color.WHITE);
        overlayLayout = new GlyphLayout();
        uiProjection = new Matrix4();

        // Spawn camera gần mặt đất để test terrain dễ hơn.
        camera.position.set(spawnX, spawnY, 0f);
        camera.update();
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }
        if (paused && (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.G))) {
            paused = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.getScreenRouter().request(ScreenId.MENU);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            game.getScreenRouter().request(ScreenId.GAME_OVER);
        }

        if (paused) {
            return;
        }

        // DUOC-ENTITY: update toàn bộ entity (Player input + Mob AI + sync physics)
        entityManager.update(delta);

        float halfW = camera.viewportWidth  * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;

        if (player != null && player.isAlive()) {
            // DUOC-ENTITY: camera follow player — clamp trong biên world
            // TODO(KIEN-WORLD): chuyển logic này sang CameraController khi có chunk system
            float targetX = player.getX() + Player.PLAYER_W / 2f;
            float targetY = player.getY() + Player.PLAYER_H / 2f;
            float followLerp = Math.min(1f, delta * 7f);
            camera.position.x += (targetX - camera.position.x) * followLerp;
            camera.position.y += (targetY - camera.position.y) * followLerp;
            camera.position.x = Math.max(halfW, Math.min(world.width  - halfW, camera.position.x));
            camera.position.y = Math.max(halfH, Math.min(world.height - halfH, camera.position.y));
        } else {
            // Fallback WASD khi player chết hoặc chưa có
            float cameraSpeed = 16f;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.position.x -= cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.position.x += cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.position.y -= cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.position.y += cameraSpeed * delta;
            camera.position.x = Math.max(halfW, Math.min(world.width  - halfW, camera.position.x));
            camera.position.y = Math.max(halfH, Math.min(world.height - halfH, camera.position.y));
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0.4f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        entityManager.render(batch); // DUOC-ENTITY: mob trước, player sau (render order)
        batch.end();

        // ── HUD / debug block palette ────────────────────────────
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(BlockPalette.GRASS,   0.25f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        batch.draw(BlockPalette.STONE,   1.35f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        batch.draw(BlockPalette.BEDROCK, 2.45f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        batch.end();

        if (paused) {
            drawPauseOverlay();
        }
    }

    private void drawPauseOverlay() {
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);

        batch.begin();
        Color prevColor = new Color(batch.getColor());
        batch.setColor(0f, 0f, 0f, 0.35f);
        batch.draw(overlayTexture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(prevColor);

        drawCentered("PAUSED", Gdx.graphics.getHeight() * 0.62f);
        drawCentered("P / ESC / G: Resume", Gdx.graphics.getHeight() * 0.48f);
        drawCentered("M: Menu   K: Game Over", Gdx.graphics.getHeight() * 0.40f);
        batch.end();
    }

    private void drawCentered(String text, float y) {
        overlayLayout.setText(overlayFont, text);
        float x = (Gdx.graphics.getWidth() - overlayLayout.width) * 0.5f;
        overlayFont.draw(batch, overlayLayout, x, y);
    }

    @Override
    public void dispose() {
        super.dispose();
        BlockPalette.dispose();
        overlayTexture.dispose();
        overlayFont.dispose();
        entityManager.dispose(); // DUOC-ENTITY: giải phóng tài nguyên player + mob
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.GAME;
    }
}
