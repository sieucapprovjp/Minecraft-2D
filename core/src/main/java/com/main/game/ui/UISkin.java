package com.main.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UISkin {
    private static Skin skin;
    private static BitmapFont font;

    public static Skin getSkin() {
        if (skin == null) {
            skin = new Skin();
            font = new BitmapFont();
            font.getData().setScale(1.5f);
            skin.add("default", font);

            // Create button textures using Pixmap
            Texture upTex = createButtonTexture(Color.valueOf("7D7D7D"), Color.valueOf("000000"), Color.valueOf("A8A8A8"));
            Texture downTex = createButtonTexture(Color.valueOf("5B5B5B"), Color.valueOf("000000"), Color.valueOf("A8A8A8"));
            Texture overTex = createButtonTexture(Color.valueOf("7D7D7D"), Color.valueOf("FFFFFF"), Color.valueOf("A8A8A8"));

            skin.add("button-up", upTex);
            skin.add("button-down", downTex);
            skin.add("button-over", overTex);

            TextButtonStyle btnStyle = new TextButtonStyle();
            btnStyle.up = new TextureRegionDrawable(skin.get("button-up", Texture.class));
            btnStyle.down = new TextureRegionDrawable(skin.get("button-down", Texture.class));
            btnStyle.over = new TextureRegionDrawable(skin.get("button-over", Texture.class));
            btnStyle.font = skin.getFont("default");
            btnStyle.fontColor = Color.WHITE;
            btnStyle.overFontColor = Color.YELLOW;
            btnStyle.downFontColor = Color.LIGHT_GRAY;
            
            skin.add("default", btnStyle);

            LabelStyle lblStyle = new LabelStyle();
            lblStyle.font = font;
            lblStyle.fontColor = Color.WHITE;
            skin.add("default", lblStyle);
        }
        return skin;
    }

    private static Texture createButtonTexture(Color bgColor, Color borderColor, Color highlightColor) {
        int width = 200;
        int height = 40;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        // Background
        pixmap.setColor(bgColor);
        pixmap.fillRectangle(0, 0, width, height);
        
        // Border
        pixmap.setColor(borderColor);
        pixmap.drawRectangle(0, 0, width, height);
        pixmap.drawRectangle(1, 1, width - 2, height - 2);

        // Highlight (top and left inner border)
        pixmap.setColor(highlightColor);
        pixmap.drawLine(2, 2, width - 3, 2); // Top
        pixmap.drawLine(2, 2, 2, height - 3); // Left

        // Dark shadow (bottom and right inner border)
        pixmap.setColor(Color.valueOf("3F3F3F"));
        pixmap.drawLine(2, height - 3, width - 3, height - 3); // Bottom
        pixmap.drawLine(width - 3, 2, width - 3, height - 3); // Right

        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    public static void dispose() {
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
    }
}
