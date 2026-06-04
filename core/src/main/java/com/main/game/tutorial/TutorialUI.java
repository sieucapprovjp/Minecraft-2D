package com.main.game.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.main.game.ui.UiFontFactory;

/**
 * Draws the interactive tutorial popup on top of the game screen.
 */
public class TutorialUI {

    private static final String[] STEP_TITLES = {
        "Step 1: Chop Wood",
        "Step 2: Craft Sticks",
        "Step 3: Crafting Table",
        "Step 4: Craft a Tool",
        "Step 5: Start Mining",
        "Step 6: Win the Raid"
    };

    private static final String[] STEP_DESCRIPTIONS = {
        "Find a tree and hold Left Mouse\non a log block to chop wood.",
        "Press E to open your inventory.\nCraft planks, then stack 2 planks vertically to make sticks.",
        "Use 4 planks in a 2x2 square to craft a Crafting Table.\nRight-click to place it in the world.",
        "Stand near the Crafting Table and press E to open the 3x3 grid.\nUse 3 planks on top and 2 sticks in the middle to craft a Wooden Pickaxe.",
        "Use the Wooden Pickaxe to mine stone and search for ore underground.\nCoal, iron, copper, gold, and diamond are useful upgrades.",
        "Place a Raid Banner inside a village to start a Raid.\nDefeat every attacker to win the game."
    };

    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout layout;
    private Texture overlayTexture;
    private Texture panelTexture;
    private Matrix4 uiProjection;

    private float gotItBtnX;
    private float gotItBtnY;
    private float gotItBtnW;
    private float gotItBtnH;

    public TutorialUI() {
        font = UiFontFactory.create(18, Color.WHITE);
        titleFont = UiFontFactory.create(26, Color.YELLOW);
        layout = new GlyphLayout();

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.BLACK);
        pix.fill();
        overlayTexture = new Texture(pix);
        pix.setColor(0.12f, 0.12f, 0.18f, 1f);
        panelTexture = new Texture(pix);
        pix.dispose();

        uiProjection = new Matrix4();
    }

    public void render(SpriteBatch batch, TutorialManager manager) {
        if (!manager.isShowing()) {
            return;
        }

        int step = manager.getActiveStep();
        int idx = step - 1;
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        uiProjection.setToOrtho2D(0, 0, sw, sh);
        batch.setProjectionMatrix(uiProjection);
        batch.begin();

        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(overlayTexture, 0, 0, sw, sh);

        float panelW = sw * 0.72f;
        float panelH = sh * 0.44f;
        float panelX = (sw - panelW) / 2f;
        float panelY = (sh - panelH) / 2f;
        batch.setColor(0.13f, 0.13f, 0.2f, 0.95f);
        batch.draw(panelTexture, panelX, panelY, panelW, panelH);
        batch.setColor(Color.WHITE);

        layout.setText(titleFont, STEP_TITLES[idx]);
        float titleX = (sw - layout.width) / 2f;
        float titleY = panelY + panelH - 25f;
        titleFont.draw(batch, layout, titleX, titleY);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        float descY = panelY + panelH * 0.58f;
        for (String line : STEP_DESCRIPTIONS[idx].split("\n")) {
            layout.setText(font, line);
            font.draw(batch, layout, (sw - layout.width) / 2f, descY);
            descY -= layout.height + 10f;
        }

        gotItBtnW = sw * 0.22f;
        gotItBtnH = sh * 0.065f;
        gotItBtnX = (sw - gotItBtnW) / 2f;
        gotItBtnY = panelY + 18f;
        batch.setColor(0.25f, 0.55f, 0.25f, 1f);
        batch.draw(panelTexture, gotItBtnX, gotItBtnY, gotItBtnW, gotItBtnH);
        batch.setColor(Color.WHITE);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.3f);
        layout.setText(font, "Got it");
        float btnTextX = gotItBtnX + (gotItBtnW - layout.width) / 2f;
        float btnTextY = gotItBtnY + (gotItBtnH + layout.height) / 2f;
        font.draw(batch, layout, btnTextX, btnTextY);

        batch.end();
    }

    public boolean handleClick(float screenX, float screenY) {
        return screenX >= gotItBtnX && screenX <= gotItBtnX + gotItBtnW
            && screenY >= gotItBtnY && screenY <= gotItBtnY + gotItBtnH;
    }

    public void dispose() {
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
        if (panelTexture != null) panelTexture.dispose();
    }
}
