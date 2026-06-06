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
 * Static Game Help screen described in doc/TUTORIAL_DESIGN.md.
 */
public class HelpScreen extends BaseScreen {

    private static final int TAB_COUNT = 4;
    private static final int TAB_CONTROLS = 0;
    private static final int TAB_CRAFTING = 1;
    private static final int TAB_MATERIALS = 2;
    private static final int TAB_GOAL = 3;

    private static final Color SCREEN_BG = new Color(0.05f, 0.05f, 0.07f, 1f);
    private static final Color PANEL_BG = new Color(0.12f, 0.12f, 0.15f, 0.98f);
    private static final Color PANEL_EDGE_LIGHT = new Color(0.54f, 0.54f, 0.60f, 1f);
    private static final Color PANEL_EDGE_DARK = new Color(0.02f, 0.02f, 0.025f, 1f);
    private static final Color CARD_BG = new Color(0.18f, 0.18f, 0.20f, 1f);
    private static final Color CARD_HEADER = new Color(0.26f, 0.26f, 0.30f, 1f);
    private static final Color TAB_BG = new Color(0.24f, 0.24f, 0.28f, 1f);
    private static final Color TAB_ACTIVE = new Color(0.42f, 0.42f, 0.48f, 1f);
    private static final Color BUTTON_BG = new Color(0.36f, 0.23f, 0.23f, 1f);
    private static final Color GOLD = new Color(1f, 0.93f, 0.10f, 1f);
    private static final Color MUTED = new Color(0.74f, 0.74f, 0.78f, 1f);

    private static final String[] TAB_NAMES = {
        "Controls", "Crafting", "Materials", "Goal"
    };

    private static final String[][] TAB_CONTENT = {
        {
            "# Movement",
            "A / D or left/right arrows: Move",
            "Space / W / up arrow: Jump",
            "1-9: Select a hotbar slot",
            "",
            "# Actions",
            "Left mouse: Chop trees, mine blocks, or attack mobs",
            "Right mouse: Place blocks or eat held food",
            "E: Open or close inventory",
            "ESC / P: Pause",
            "M: Return to menu",
            "F1: Open Game Help"
        },
        {
            "# Basic recipes",
            "1 wood -> 4 planks",
            "2 planks stacked vertically -> 4 stick",
            "4 planks in a 2x2 square -> crafting_table",
            "8 cobblestone around the center of a 3x3 grid -> furnace",
            "8 planks around the center of a 3x3 grid -> chest",
            "",
            "# Starter path",
            "Chop a tree -> craft planks -> craft sticks -> craft a crafting_table",
            "Place the crafting_table, aim at it, then press E to open the 3x3 grid.",
            "Wooden Pickaxe: 3 planks on top + 2 sticks in the center column.",
            "Sword, axe, shovel, and hoe use Minecraft-style material patterns."
        },
        {
            "# Materials",
            "Wood: First resource, used for planks, sticks, and crafting_table.",
            "Stone / Cobblestone: Mine with a pickaxe to upgrade to stone tools.",
            "Coal: Fuel for the furnace.",
            "Copper / Iron / Gold: Mine ore for raw material, then smelt it into ingots.",
            "Diamond: Advanced material for stronger tools and armor.",
            "Netherite: Highest tool material currently registered.",
            "",
            "# Use the right tool",
            "Pickaxe: stone, deepslate, sandstone, and ore.",
            "Axe: wood, planks, leaves.",
            "Shovel: dirt, grass, sand, snow.",
            "Sword: Best for fighting mobs.",
            "Higher-tier tools mine faster and last longer; gold is fast but fragile."
        },
        {
            "# Final goal",
            "1. Survive and gather resources.",
            "2. Craft a crafting_table, tools, furnace, chest, and armor.",
            "3. Find a village in the world.",
            "4. Place a Raid Banner in the village to start a Raid.",
            "5. Prepare weapons, armor, and food.",
            "6. Defeat every attacker to win.",
            "",
            "# Raid enemies",
            "Pillager: Ranged attacker.",
            "Vindicator: Melee attacker.",
            "Evoker: Spell caster.",
            "Ravager: Large dangerous mob."
        }
    };

    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout layout;
    private Matrix4 uiProjection;
    private Texture bgTexture;
    private Texture pixelTexture;
    private int activeTab;

    private float panelX;
    private float panelY;
    private float panelW;
    private float panelH;
    private float tabY;
    private float tabH;
    private float backX;
    private float backY;
    private float backW;
    private float backH;

    public HelpScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        layout = new GlyphLayout();
        uiProjection = new Matrix4();
        bgTexture = new Texture(StageBackgrounds.random());
        font = UiFontFactory.create(22, Color.WHITE);
        titleFont = UiFontFactory.create(32, Color.YELLOW);

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        pixelTexture = new Texture(pix);
        pix.dispose();

        activeTab = TAB_CONTROLS;
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
            || Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            goBack();
            return;
        }

        if (!Gdx.input.justTouched()) {
            return;
        }

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        updateLayout(sw, sh);

        float mx = Gdx.input.getX();
        float my = sh - Gdx.input.getY();

        if (handleTabClick(mx, my)) {
            return;
        }
        if (mx >= backX && mx <= backX + backW && my >= backY && my <= backY + backH) {
            game.getAudioManager().play(AudioId.UI_CLICK);
            goBack();
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(SCREEN_BG.r, SCREEN_BG.g, SCREEN_BG.b, SCREEN_BG.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        updateLayout(sw, sh);
        uiProjection.setToOrtho2D(0, 0, sw, sh);
        batch.setProjectionMatrix(uiProjection);

        batch.begin();
        drawBackground(sw, sh);
        drawMainPanel();
        drawHeader();
        drawTabs();
        drawContent();
        drawBackButton();
        batch.end();
    }

    private void updateLayout(float sw, float sh) {
        panelW = Math.min(sw - 56f, 1120f);
        panelH = Math.min(sh - 56f, 690f);
        panelX = (sw - panelW) / 2f;
        panelY = (sh - panelH) / 2f;
        tabH = 46f;
        tabY = panelY + panelH - 118f;
        backW = 132f;
        backH = 38f;
        backX = panelX + panelW - backW - 28f;
        backY = panelY + 24f;
    }

    private boolean handleTabClick(float mx, float my) {
        float gap = 8f;
        float tabW = (panelW - 56f - gap * (TAB_COUNT - 1)) / TAB_COUNT;
        float x = panelX + 28f;
        for (int i = 0; i < TAB_COUNT; i++) {
            if (mx >= x && mx <= x + tabW && my >= tabY && my <= tabY + tabH) {
                if (activeTab != i) {
                    activeTab = i;
                    game.getAudioManager().play(AudioId.UI_CLICK);
                }
                return true;
            }
            x += tabW + gap;
        }
        return false;
    }

    private void drawBackground(float sw, float sh) {
        if (bgTexture != null) {
            drawCover(bgTexture, sw, sh);
        } else {
            batch.setColor(0.03f, 0.03f, 0.04f, 1f);
            batch.draw(pixelTexture, 0, 0, sw, sh);
        }
        batch.setColor(0f, 0f, 0f, 0.62f);
        batch.draw(pixelTexture, 0, 0, sw, sh);
        batch.setColor(0.10f, 0.10f, 0.13f, 0.72f);
        batch.draw(pixelTexture, 0, 0, sw, sh * 0.12f);
        batch.draw(pixelTexture, 0, sh * 0.88f, sw, sh * 0.12f);
    }

    private void drawCover(Texture texture, float sw, float sh) {
        float scale = Math.max(sw / texture.getWidth(), sh / texture.getHeight());
        float drawW = texture.getWidth() * scale;
        float drawH = texture.getHeight() * scale;
        batch.setColor(Color.WHITE);
        batch.draw(texture, (sw - drawW) / 2f, (sh - drawH) / 2f, drawW, drawH);
    }

    private void drawMainPanel() {
        drawBeveledRect(panelX, panelY, panelW, panelH, PANEL_BG, PANEL_EDGE_LIGHT, PANEL_EDGE_DARK);
        batch.setColor(0.07f, 0.07f, 0.09f, 1f);
        batch.draw(pixelTexture, panelX + 10f, panelY + 10f, panelW - 20f, panelH - 20f);
    }

    private void drawHeader() {
        float headerY = panelY + panelH - 74f;
        batch.setColor(0.16f, 0.16f, 0.19f, 1f);
        batch.draw(pixelTexture, panelX + 14f, headerY, panelW - 28f, 58f);

        titleFont.setColor(GOLD);
        titleFont.getData().setScale(1f);
        layout.setText(titleFont, "Game Help");
        titleFont.draw(batch, layout, panelX + 34f, headerY + 38f);

        font.setColor(MUTED);
        font.getData().setScale(0.78f);
        layout.setText(font, "Press F1 or ESC to go back");
        font.draw(batch, layout, panelX + panelW - layout.width - 34f, headerY + 35f);
    }

    private void drawTabs() {
        float gap = 8f;
        float tabW = (panelW - 56f - gap * (TAB_COUNT - 1)) / TAB_COUNT;
        float x = panelX + 28f;

        font.getData().setScale(0.92f);
        for (int i = 0; i < TAB_COUNT; i++) {
            boolean active = i == activeTab;
            drawBeveledRect(x, tabY, tabW, tabH, active ? TAB_ACTIVE : TAB_BG,
                active ? new Color(0.78f, 0.78f, 0.84f, 1f) : new Color(0.48f, 0.48f, 0.54f, 1f),
                PANEL_EDGE_DARK);
            if (active) {
                batch.setColor(GOLD);
                batch.draw(pixelTexture, x + 6f, tabY + 5f, tabW - 12f, 3f);
            }

            font.setColor(Color.WHITE);
            layout.setText(font, TAB_NAMES[i]);
            font.draw(batch, layout,
                x + (tabW - layout.width) / 2f,
                tabY + (tabH + layout.height) / 2f + 1f);
            x += tabW + gap;
        }
    }

    private void drawContent() {
        float contentX = panelX + 28f;
        float contentY = panelY + 82f;
        float contentW = panelW - 56f;
        float contentH = tabY - contentY - 18f;
        drawBeveledRect(contentX, contentY, contentW, contentH,
            new Color(0.11f, 0.11f, 0.13f, 1f), new Color(0.32f, 0.32f, 0.36f, 1f), PANEL_EDGE_DARK);

        String[][] sections = splitSections(TAB_CONTENT[activeTab]);
        float gap = 16f;
        boolean twoColumns = contentW >= 760f && sections.length > 1;
        float cardW = twoColumns ? (contentW - gap - 24f) / 2f : contentW - 24f;
        float cardX = contentX + 12f;
        float topY = contentY + contentH - 18f;

        for (int i = 0; i < sections.length; i++) {
            if (twoColumns) {
                float x = cardX + (i % 2) * (cardW + gap);
                float cardH = contentH - 36f;
                drawSectionCard(sections[i], x, topY - cardH, cardW, cardH);
            } else {
                float cardH = (contentH - 48f) / Math.max(1, sections.length);
                drawSectionCard(sections[i], cardX, topY - cardH - i * (cardH + gap), cardW, cardH);
            }
        }
    }

    private void drawBackButton() {
        drawBeveledRect(backX, backY, backW, backH, BUTTON_BG,
            new Color(0.62f, 0.42f, 0.42f, 1f), new Color(0.12f, 0.05f, 0.05f, 1f));
        font.setColor(Color.WHITE);
        font.getData().setScale(0.88f);
        layout.setText(font, "Back");
        font.draw(batch, layout,
            backX + (backW - layout.width) / 2f,
            backY + (backH + layout.height) / 2f + 1f);
    }

    private void drawSectionCard(String[] section, float x, float y, float w, float h) {
        drawBeveledRect(x, y, w, h, CARD_BG, new Color(0.42f, 0.42f, 0.46f, 1f), PANEL_EDGE_DARK);
        batch.setColor(CARD_HEADER);
        batch.draw(pixelTexture, x + 5f, y + h - 40f, w - 10f, 34f);

        font.setColor(GOLD);
        font.getData().setScale(0.96f);
        layout.setText(font, section[0]);
        font.draw(batch, layout, x + 18f, y + h - 16f);

        font.setColor(Color.WHITE);
        font.getData().setScale(0.92f);
        float cursorY = y + h - 60f;
        for (int i = 1; i < section.length; i++) {
            cursorY = drawWrappedLine(section[i], x + 18f, cursorY, w - 36f, 23f);
        }
    }

    private float drawWrappedLine(String text, float x, float y, float maxW, float lineH) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            layout.setText(font, candidate);
            if (layout.width > maxW && line.length() > 0) {
                font.draw(batch, line.toString(), x, y);
                y -= lineH;
                line.setLength(0);
                line.append(word);
            } else {
                line.setLength(0);
                line.append(candidate);
            }
        }
        if (line.length() > 0) {
            font.draw(batch, line.toString(), x, y);
            y -= lineH;
        }
        return y - 4f;
    }

    private String[][] splitSections(String[] lines) {
        String[][] sections = new String[2][];
        String[] first = new String[8];
        String[] second = new String[8];
        int firstCount = 0;
        int secondCount = 0;
        boolean useSecond = false;

        for (String raw : lines) {
            if (raw.isEmpty()) {
                continue;
            }
            if (raw.startsWith("# ")) {
                if (firstCount > 0) {
                    useSecond = true;
                }
                raw = raw.substring(2);
            }
            if (useSecond) {
                second[secondCount++] = raw;
            } else {
                first[firstCount++] = raw;
            }
        }

        sections[0] = copy(first, firstCount);
        sections[1] = copy(second, secondCount);
        return sections;
    }

    private String[] copy(String[] source, int count) {
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = source[i];
        }
        return result;
    }

    private void drawBeveledRect(float x, float y, float w, float h, Color fill, Color light, Color dark) {
        batch.setColor(fill);
        batch.draw(pixelTexture, x, y, w, h);
        batch.setColor(light);
        batch.draw(pixelTexture, x, y + h - 2f, w, 2f);
        batch.draw(pixelTexture, x, y, 2f, h);
        batch.setColor(dark);
        batch.draw(pixelTexture, x, y, w, 2f);
        batch.draw(pixelTexture, x + w - 2f, y, 2f, h);
        batch.setColor(Color.WHITE);
    }

    private void goBack() {
        game.getScreenRouter().requestHelpReturn();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        super.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (pixelTexture != null) pixelTexture.dispose();
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.HELP;
    }
}
