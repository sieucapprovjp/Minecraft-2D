package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.main.game.MainGame;
import com.main.game.navigation.ScreenId;

public class StateScreen extends BaseScreen {

    private final ScreenId id;
    private BitmapFont font;
    private GlyphLayout layout;
    private Matrix4 uiProjection;

    private Texture btnTex;
    private Rectangle btn1Rect, btn2Rect;

    public StateScreen(MainGame game, ScreenId id) {
        super(game);
        this.id = id;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        // Make font a bit bigger if possible, libgdx default font is small
        this.font.getData().setScale(1.5f);
        this.layout = new GlyphLayout();
        this.uiProjection = new Matrix4();

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        this.btnTex = new Texture(pix);
        pix.dispose();

        btn1Rect = new Rectangle();
        btn2Rect = new Rectangle();
    }

    @Override
    public void update(float delta) {
        // Mouse click logic
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY(); // Unproject Y

            if (btn1Rect.contains(mx, my)) {
                if (id == ScreenId.MENU || id == ScreenId.PAUSE) {
                    game.getScreenRouter().request(ScreenId.GAME);
                } else if (id == ScreenId.GAME_OVER) {
                    game.getScreenRouter().request(ScreenId.GAME);
                }
            } else if (btn2Rect.contains(mx, my)) {
                if (id == ScreenId.MENU) {
                    Gdx.app.exit();
                } else if (id == ScreenId.PAUSE || id == ScreenId.GAME_OVER) {
                    game.getScreenRouter().request(ScreenId.MENU);
                }
            }
        }

        // Hotkeys
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            if (id == ScreenId.MENU || id == ScreenId.PAUSE || id == ScreenId.GAME_OVER) {
                game.getScreenRouter().request(ScreenId.GAME);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.getScreenRouter().request(ScreenId.MENU);
        }
    }

    @Override
    public void draw() {
        if (id == ScreenId.PAUSE) {
            Gdx.gl.glClearColor(0.15f, 0.15f, 0.18f, 1f);
        } else if (id == ScreenId.GAME_OVER) {
            Gdx.gl.glClearColor(0.28f, 0.08f, 0.08f, 1f);
        } else {
            Gdx.gl.glClearColor(0.10f, 0.12f, 0.16f, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String title;
        if (id == ScreenId.PAUSE) title = "PAUSED";
        else if (id == ScreenId.GAME_OVER) title = "GAME OVER";
        else title = "MINECRAFT 2D";

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float bw = 250f;
        float bh = 50f;

        btn1Rect.set((sw - bw) / 2f, sh * 0.45f, bw, bh);
        btn2Rect.set((sw - bw) / 2f, sh * 0.30f, bw, bh);

        uiProjection.setToOrtho2D(0, 0, sw, sh);
        batch.setProjectionMatrix(uiProjection);
        batch.begin();

        // Tựa game
        font.getData().setScale(2.5f);
        font.setColor(Color.YELLOW);
        drawCentered(title, sh * 0.75f);

        // Nút bấm
        float mx = Gdx.input.getX();
        float my = sh - Gdx.input.getY();

        // Button 1
        batch.setColor(btn1Rect.contains(mx, my) ? Color.GRAY : Color.DARK_GRAY);
        batch.draw(btnTex, btn1Rect.x, btn1Rect.y, btn1Rect.width, btn1Rect.height);

        // Button 2
        batch.setColor(btn2Rect.contains(mx, my) ? Color.GRAY : Color.DARK_GRAY);
        batch.draw(btnTex, btn2Rect.x, btn2Rect.y, btn2Rect.width, btn2Rect.height);

        batch.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        String text1 = (id == ScreenId.MENU || id == ScreenId.PAUSE) ? "RESUME / PLAY" : "PLAY AGAIN";
        String text2 = (id == ScreenId.MENU) ? "QUIT" : "MAIN MENU";

        drawCentered(text1, btn1Rect.y + bh * 0.65f);
        drawCentered(text2, btn2Rect.y + bh * 0.65f);

        batch.end();
    }

    private void drawCentered(String text, float y) {
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) * 0.5f;
        font.draw(batch, layout, x, y);
    }

    @Override
    public ScreenId getScreenId() {
        return id;
    }

    @Override
    public void dispose() {
        font.dispose();
        btnTex.dispose();
    }
}
