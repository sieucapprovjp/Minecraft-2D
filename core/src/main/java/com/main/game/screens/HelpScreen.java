package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.main.game.MainGame;
import com.main.game.audio.AudioId;
import com.main.game.navigation.ScreenId;
import com.main.game.ui.UiFontFactory;

/**
 * Màn hình trợ giúp (Help) với 4 tab nội dung tĩnh.
 * Truy cập từ nút Help ở MenuScreen hoặc phím F1 trong GameScreen.
 */
public class HelpScreen extends BaseScreen {

    private static final int TAB_CONTROLS = 0;
    private static final int TAB_CRAFTING = 1;
    private static final int TAB_MATERIALS = 2;
    private static final int TAB_GOAL = 3;
    private static final int TAB_COUNT = 4;

    private static final String[] TAB_NAMES = {
        "Điều khiển", "Chế tạo", "Vật liệu", "Mục tiêu"
    };

    // Nội dung cho từng tab
    private static final String[][] TAB_CONTENT = {
        // TAB_CONTROLS
        {
            "DI CHUYỂN: WASD",
            "NHẢY: Space",
            "MỞ KHO ĐỒ: E",
            "CHẶT/ĐẬP: Giữ Chuột Trái",
            "ĐẶT BLOCK: Chuột Phải",
            "ĂN: Chuột Phải (khi cầm đồ ăn)",
            "TẤN CÔNG: Chuột Trái (khi cầm vũ khí)",
            "TRỢ GIÚP: F1",
            "TẠM DỪNG: ESC / P",
            "VỀ MENU: M"
        },
        // TAB_CRAFTING
        {
            "CÁC CÔNG THỨC CƠ BẢN:",
            "",
            "Ván gỗ (4): 1 khúc gỗ",
            "Que (4): 2 ván gỗ chồng dọc",
            "Bàn chế tạo: 4 ván gỗ (2x2)",
            "Lò nung: 8 đá cuội (ring 3x3)",
            "Rương: 8 ván gỗ (ring 3x3)",
            "",
            "CÚP GỖ: 3 ván gỗ trên + 2 que giữa",
            "CÚP ĐÁ: 3 đá cuội trên + 2 que giữa",
            "CÚP SẮT: 3 thỏi sắt trên + 2 que giữa",
            "CÚP KIM CƯƠNG: 3 kim cương trên + 2 que",
            "",
            "Rìu, Xẻng, Cuốc: công thức tương tự"
        },
        // TAB_MATERIALS
        {
            "CÁC LOẠI QUẶNG & CÔNG CỤ:",
            "",
            "THAN (Coal): tầng nông, dùng làm nhiên liệu",
            "SẮT (Iron): tầng trung bình, cần cúp đá+",
            "ĐỒNG (Copper): tầng trung bình",
            "VÀNG (Gold): tầng sâu, cần cúp sắt+",
            "KIM CƯƠNG (Diamond): tầng rất sâu, cần cúp sắt+",
            "LAPIS: tầng trung bình",
            "REDSTONE: tầng sâu",
            "",
            "CẤP ĐỘ CÔNG CỤ:",
            "Gỗ → Đá → Sắt → Kim Cương"
        },
        // TAB_GOAL
        {
            "MỤC TIÊU CUỐI CÙNG:",
            "",
            "1. Tìm một ngôi làng (Village)",
            "2. Đặt Banner cướp (Raid Banner) trong làng",
            "3. Chuẩn bị vũ khí và giáp",
            "4. Tiêu diệt tất cả kẻ tấn công:",
            "   • Pillager (bắn cung)",
            "   • Vindicator (cận chiến)",
            "   • Evoker (phép thuật)",
            "   • Ravager (quái thú lớn)",
            "5. Tiêu diệt hết Raid để chiến thắng!"
        }
    };

    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout layout;
    private Matrix4 uiProjection;
    private Texture pixelTexture; // 1x1 cho nút tab và back button

    private int activeTab;

    public HelpScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        layout = new GlyphLayout();
        uiProjection = new Matrix4();

        font = UiFontFactory.create(18, Color.WHITE);
        titleFont = UiFontFactory.create(24, Color.YELLOW);

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        pixelTexture = new Texture(pix);
        pix.dispose();

        activeTab = 0;
    }

    @Override
    public void update(float delta) {
        // Back bằng phím ESC hoặc F1
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
            || Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            goBack();
            return;
        }

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float mx = Gdx.input.getX();
        float my = sh - Gdx.input.getY();
        boolean clicked = Gdx.input.justTouched();

        if (!clicked) return;

        // Kiểm tra click vào tab
        float tabW = sw / TAB_COUNT;
        float tabH = 50f;
        float tabY = sh - tabH;
        for (int i = 0; i < TAB_COUNT; i++) {
            float tabX = i * tabW;
            if (mx >= tabX && mx <= tabX + tabW && my >= tabY && my <= tabY + tabH) {
                if (activeTab != i) {
                    activeTab = i;
                    game.getAudioManager().play(AudioId.UI_CLICK);
                }
                return;
            }
        }

        // Kiểm tra click vào Back button
        float backBtnX = 20f;
        float backBtnY = sh - tabH - 55f;
        float backBtnW = 80f;
        float backBtnH = 36f;
        if (mx >= backBtnX && mx <= backBtnX + backBtnW
            && my >= backBtnY && my <= backBtnY + backBtnH) {
            game.getAudioManager().play(AudioId.UI_CLICK);
            goBack();
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        uiProjection.setToOrtho2D(0, 0, sw, sh);
        batch.setProjectionMatrix(uiProjection);
        batch.begin();

        drawTabs(sw, sh);
        drawBackButton(sw, sh);
        drawTabContent(sw, sh);

        batch.end();
    }

    private void drawTabs(float sw, float sh) {
        float tabW = sw / TAB_COUNT;
        float tabH = 50f;
        float tabY = sh - tabH;

        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);

        for (int i = 0; i < TAB_COUNT; i++) {
            float tabX = i * tabW;
            if (i == activeTab) {
                batch.setColor(0.25f, 0.25f, 0.35f, 1f);
            } else {
                batch.setColor(0.15f, 0.15f, 0.22f, 1f);
            }
            batch.draw(pixelTexture, tabX, tabY, tabW, tabH);

            // Đường viền dưới tab đang active
            if (i == activeTab) {
                batch.setColor(0.4f, 0.7f, 1.0f, 1f);
                batch.draw(pixelTexture, tabX, tabY, tabW, 3f);
            }

            batch.setColor(Color.WHITE);
            layout.setText(font, TAB_NAMES[i]);
            font.draw(batch, layout,
                tabX + (tabW - layout.width) / 2f,
                tabY + (tabH + layout.height) / 2f);
        }
    }

    private void drawBackButton(float sw, float sh) {
        float tabH = 50f;
        float btnX = 20f;
        float btnY = sh - tabH - 50f;
        float btnW = 80f;
        float btnH = 34f;

        batch.setColor(0.4f, 0.2f, 0.2f, 1f);
        batch.draw(pixelTexture, btnX, btnY, btnW, btnH);
        batch.setColor(Color.WHITE);

        font.getData().setScale(1.0f);
        layout.setText(font, "Quay lại");
        font.draw(batch, layout,
            btnX + (btnW - layout.width) / 2f,
            btnY + (btnH + layout.height) / 2f);
    }

    private void drawTabContent(float sw, float sh) {
        String[] lines = TAB_CONTENT[activeTab];
        float tabH = 50f;
        float startY = sh - tabH - 25f;
        float lineHeight = 22f;

        font.setColor(Color.WHITE);
        font.getData().setScale(0.9f);

        // Tiêu đề tab đậm hơn
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                startY -= lineHeight * 0.5f;
                continue;
            }
            if (line.equals(line.toUpperCase()) && line.length() > 3) {
                // Dòng in hoa = heading → màu vàng, scale lớn hơn
                font.setColor(Color.YELLOW);
                font.getData().setScale(1.1f);
            } else {
                font.setColor(Color.WHITE);
                font.getData().setScale(0.9f);
            }
            layout.setText(font, line);
            // Thụt lề nếu bắt đầu bằng "•" hoặc số
            float x = (line.startsWith("   •") || Character.isDigit(line.charAt(0)))
                ? 60f : 40f;
            font.draw(batch, layout, x, startY);
            startY -= lineHeight;
        }
    }

    private void goBack() {
        game.getScreenRouter().request(ScreenId.MENU);
    }

    @Override
    public void resize(int width, int height) {
        // Dùng screen coordinates nên không cần resize
    }

    @Override
    public void dispose() {
        super.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (pixelTexture != null) pixelTexture.dispose();
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.HELP;
    }
}
