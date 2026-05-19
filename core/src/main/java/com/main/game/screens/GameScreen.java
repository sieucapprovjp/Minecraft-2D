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
import com.badlogic.gdx.math.Vector2;
import com.main.game.MainGame;
import com.main.game.entities.EntityManager;
import com.main.game.entities.Mob;
import com.main.game.entities.Player;
import com.main.game.navigation.ScreenId;
import com.main.game.physics.PhysicsEngine;
import com.main.game.utils.Constants;
import com.main.game.world.BlockPalette;
import com.main.game.world.DemoBlockViewer;
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
    private BitmapFont font;

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {

        long currentSeed = System.currentTimeMillis();

        world = new World(currentSeed);
        // TODO(KIEN-WORLD): seed nên lấy từ save/game config thay vì hardcode.

        camera.position.set(world.width / 2f, world.height / 2f, 0f);
        world.update(camera);
        // world.generate(1337L);

        physics = new PhysicsEngine();

        // ── Khởi tạo Player ─────────────────────────── DUOC-ENTITY ──
        Vector2 spawn = world.getSpawnPoint();
        float spawnX = spawn.x;
        float spawnY = spawn.y;
        player = new Player(spawnX, spawnY, physics, world);

        // ── Khởi tạo EntityManager ───────────────────── DUOC-ENTITY ──
        entityManager = new EntityManager();
        entityManager.setPlayer(player);

        // ── Spawn mob mẫu để test ────────────────────── DUOC-ENTITY ──
        entityManager.addMob(new Mob(spawnX + 10f, spawnY + 5f, Mob.MobType.ZOMBIE,    player, physics, world));
        entityManager.addMob(new Mob(spawnX + 20f, spawnY + 5f, Mob.MobType.SKELETON,  player, physics, world));

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
        font = new BitmapFont();
        font.setColor(Color.WHITE);

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            int sx = Math.max(2, (int) player.getX());
            int sy = Math.max(2, (int) player.getY());
            DemoBlockViewer.populateDemo(world, sx, sy);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            game.getScreenRouter().request(ScreenId.GAME_OVER);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            player.ban();
        }

        if (paused) {
            return;
        }

        // DUOC-ENTITY: update toàn bộ entity (Player input + Mob AI + sync physics)
        entityManager.update(delta);

        // update
        world.update(camera);

        // Chết -> Game Over
        if (player.getHealth() <= 0) {
            game.getScreenRouter().request(ScreenId.GAME_OVER);
        }

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

            // Xóa giới hạn (clamp) theo world.width để map chạy vô tận
            // Chỉ giữ lại giới hạn đáy màn hình (y >= halfH) nếu không muốn rớt ra ngoài khoảng không
            camera.position.y = Math.max(halfH, camera.position.y);

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
        // ─── XỬ LÝ MÀU NỀN THEO ĐỘ SÂU (DEPTH FADE) ───────────────
        // Mặt đất thường ở khoảng Y = 64 (world.height / 2). Từ đây trở lên là sáng rực.
        float surfaceY = world.height / 2f;

        // Độ sâu bắt đầu tối đen như mực (mốc Y = 20).
        float deepCaveY = 20f;

        // Tính toán tỷ lệ ánh sáng (từ 0.0 đến 1.0) dựa vào vị trí camera
        float lightRatio = (camera.position.y - deepCaveY) / (surfaceY - deepCaveY);

        // Dùng Math.max và Math.min để "kẹp" (clamp) giá trị luôn nằm trong khoảng 0.0 -> 1.0
        lightRatio = Math.max(0f, Math.min(1f, lightRatio));

        // Nội suy màu: Bầu trời gốc (0.4, 0.7, 1.0) chuyển dần sang màu xám đen (0.02, 0.02, 0.05)
        float r = (0.4f * lightRatio) + (0.02f * (1 - lightRatio));
        float g = (0.7f * lightRatio) + (0.02f * (1 - lightRatio));
        float b = (1.0f * lightRatio) + (0.05f * (1 - lightRatio));

        // Set màu nền mới và clear màn hình
        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // ──────────────────────────────────────────────────────────

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        entityManager.render(batch); // DUOC-ENTITY: mob trước, player sau (render order)
        batch.end();

        // ── HUD / debug block palette ────────────────────────────
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        if (BlockPalette.getGrass() != null) {
            batch.draw(BlockPalette.getGrass(), 0.25f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }
        if (BlockPalette.getStone() != null) {
            batch.draw(BlockPalette.getStone(), 1.35f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }
        if (BlockPalette.getBedrock() != null) {
            batch.draw(BlockPalette.getBedrock(), 2.45f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }

        // ── Text HUD ─────────────────────────────────────────────────
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);

        font.setColor(Color.RED);
        font.draw(batch, "HP: " + player.getHealth() + " / " + player.getMaxHealth(), 20, Gdx.graphics.getHeight() - 20);

        font.setColor(Color.WHITE);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "X: " + (int)player.getX() + "  Y: " + (int)player.getY(), 20, Gdx.graphics.getHeight() - 60);

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
        font.dispose();
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
