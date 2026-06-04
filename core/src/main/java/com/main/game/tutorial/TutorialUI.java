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
 * Vẽ popup hướng dẫn (tutorial) lên màn hình.
 * Popup gồm: nền mờ + panel trung tâm + tiêu đề + mô tả + nút "Đã hiểu!".
 * Tọa độ tính theo screen pixel (0,0 ở bottom-left).
 */
public class TutorialUI {

    private static final String[] STEP_TITLES = {
        "Bước 1: Chặt gỗ",
        "Bước 2: Tạo Que",
        "Bước 3: Bàn chế tạo",
        "Bước 4: Chế tạo Tool",
        "Bước 5: Khai thác Mine",
        "Bước 6: Chiến thắng Raid"
    };

    private static final String[] STEP_DESCRIPTIONS = {
        "Hãy tìm một cái cây và nhấn giữ Chuột Trái\nđể chặt gỗ. Gỗ là nguyên liệu cơ bản nhất!",
        "Mở túi đồ (phím E) và đặt 2 Ván gỗ (Planks)\nlên bàn chế tạo 2x2 để tạo Que (Stick).",
        "Dùng 4 Ván gỗ để chế tạo Bàn chế tạo\n(Crafting Table). Nhấn Chuột Phải để đặt xuống đất.",
        "Đứng gần Bàn chế tạo, nhấn E để mở chế tạo 3x3.\nĐặt 3 Ván gỗ trên + 2 Que giữa để tạo Cúp gỗ.",
        "Dùng Cúp gỗ để đào đá và tìm quặng sâu dưới\nlòng đất. Quặng sắt và than rất hữu ích!",
        "Đặt Banner cướp ở ngôi làng để triệu hồi Raid.\nTiêu diệt tất cả kẻ thù để chiến thắng!"
    };

    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout layout;
    private Texture overlayTexture; // 1x1 đen
    private Texture panelTexture;   // 1x1 xám
    private Matrix4 uiProjection;

    // Vùng nút "Đã hiểu!" (update mỗi frame render)
    private float gotItBtnX, gotItBtnY, gotItBtnW, gotItBtnH;

    public TutorialUI() {
        font = UiFontFactory.create(18, Color.WHITE);
        titleFont = UiFontFactory.create(26, Color.YELLOW);
        layout = new GlyphLayout();

        // Tạo texture 1x1 cho overlay và panel
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.BLACK);
        pix.fill();
        overlayTexture = new Texture(pix);
        pix.setColor(0.12f, 0.12f, 0.18f, 1f);
        panelTexture = new Texture(pix);
        pix.dispose();

        uiProjection = new Matrix4();
    }

    /**
     * Render popup tutorial nếu TutorialManager đang hiển thị bước nào đó.
     * Gọi sau tất cả overlay trong GameScreen.draw().
     */
    public void render(SpriteBatch batch, TutorialManager manager) {
        if (!manager.isShowing()) return;

        int step = manager.getActiveStep();
        int idx = step - 1;
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        uiProjection.setToOrtho2D(0, 0, sw, sh);
        batch.setProjectionMatrix(uiProjection);
        batch.begin();

        // ── Overlay mờ ──
        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(overlayTexture, 0, 0, sw, sh);

        // ── Panel nền ──
        float panelW = sw * 0.7f;
        float panelH = sh * 0.42f;
        float panelX = (sw - panelW) / 2f;
        float panelY = (sh - panelH) / 2f;
        batch.setColor(0.13f, 0.13f, 0.2f, 0.95f);
        batch.draw(panelTexture, panelX, panelY, panelW, panelH);
        batch.setColor(Color.WHITE);

        // ── Title ──
        layout.setText(titleFont, STEP_TITLES[idx]);
        float titleX = (sw - layout.width) / 2f;
        float titleY = panelY + panelH - 25f;
        titleFont.draw(batch, layout, titleX, titleY);

        // ── Description (hỗ trợ \n) ──
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        String desc = STEP_DESCRIPTIONS[idx];
        float descY = panelY + panelH * 0.55f;
        for (String line : desc.split("\n")) {
            layout.setText(font, line);
            font.draw(batch, layout, (sw - layout.width) / 2f, descY);
            descY -= layout.height + 10f;
        }

        // ── Nút "Đã hiểu!" ──
        gotItBtnW = sw * 0.22f;
        gotItBtnH = sh * 0.065f;
        gotItBtnX = (sw - gotItBtnW) / 2f;
        gotItBtnY = panelY + 18f;
        batch.setColor(0.25f, 0.55f, 0.25f, 1f);
        batch.draw(panelTexture, gotItBtnX, gotItBtnY, gotItBtnW, gotItBtnH);
        batch.setColor(Color.WHITE);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.3f);
        layout.setText(font, "Đã hiểu!");
        float btnTextX = gotItBtnX + (gotItBtnW - layout.width) / 2f;
        float btnTextY = gotItBtnY + (gotItBtnH + layout.height) / 2f;
        font.draw(batch, layout, btnTextX, btnTextY);

        batch.end();
    }

    /**
     * Kiểm tra xem click có trúng nút "Đã hiểu!" không.
     * @param screenX tọa độ x screen (từ Gdx.input.getX())
     * @param screenY tọa độ y screen (đã flip: Gdx.graphics.getHeight() - Gdx.input.getY())
     * @return true nếu click trúng nút
     */
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
