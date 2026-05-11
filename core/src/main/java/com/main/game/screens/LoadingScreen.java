package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.main.game.MainGame;
import com.main.game.navigation.ScreenId;
import com.main.game.utils.TextureManager;
import com.main.game.world.BlockPalette;
import com.main.game.ui.UISkin;

public class LoadingScreen extends BaseScreen {

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;
    private float progress = 0f;

    public LoadingScreen(MainGame game) {
        super(game);
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);
        layout = new GlyphLayout();
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.LOADING;
    }

    @Override
    public void update(float delta) {
        progress += delta * 0.5f;
        if (progress >= 1f) {
            progress = 1f;
            BlockPalette.getGrass(); // force lazy init
            TextureManager.getInstance();
            UISkin.getSkin();
            game.getScreenRouter().request(ScreenId.MENU);
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float barWidth = width * 0.6f;
        float barHeight = 40f;
        float barX = (width - barWidth) / 2f;
        float barY = height * 0.3f;
        
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);
        shapeRenderer.end();

        batch.begin();
        layout.setText(font, "LOADING... " + (int)(progress * 100) + "%");
        font.draw(batch, layout, (width - layout.width) / 2f, barY + barHeight + 40f);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}
