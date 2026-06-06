package com.main.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Creates UI fonts from the bundled game font asset.
 */
public final class UiFontFactory {
    private static final String PROJECT_PIXEL_FONT = "fonts/2c90030680a2fafd21f53fd39a0862e7.otf";

    private static final String VIETNAMESE_CHARACTERS =
        FreeTypeFontGenerator.DEFAULT_CHARS +
        "\n\t" +
        "àáảãạăằắẳẵặâầấẩẫậèéẻẽẹêềếểễệđìíỉĩị" +
        "òóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ" +
        "ÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỂỄỆĐÌÍỈĨỊ" +
        "ÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴ" +
        "→•";

    private UiFontFactory() {
    }

    public static BitmapFont create(int size, Color color) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(PROJECT_PIXEL_FONT));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.characters = VIETNAMESE_CHARACTERS;
        params.size = size;
        params.color = color;
        BitmapFont font = generator.generateFont(params);
        generator.dispose();
        return font;
    }
}
