package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.main.game.MainGame;
import com.main.game.navigation.ScreenId;
import com.main.game.ui.UISkin;

public class ModeSelectScreen extends BaseScreen {

    private Stage stage;

    public ModeSelectScreen(MainGame game) {
        super(game);
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.MODE_SELECT;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Select Game Mode", UISkin.getSkin());
        titleLabel.setFontScale(2f);

        TextButton survivalBtn = new TextButton("Survival Mode", UISkin.getSkin());
        TextButton creativeBtn = new TextButton("Creative Mode", UISkin.getSkin());
        TextButton backBtn = new TextButton("Back", UISkin.getSkin());

        survivalBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreenRouter().request(ScreenId.GAME);
            }
        });

        creativeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreenRouter().request(ScreenId.GAME);
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreenRouter().request(ScreenId.MENU);
            }
        });

        table.add(titleLabel).padBottom(60).row();
        table.add(survivalBtn).width(400).height(60).padBottom(20).row();
        table.add(creativeBtn).width(400).height(60).padBottom(20).row();
        table.add(backBtn).width(200).height(60).padBottom(20).row();
    }

    @Override
    public void update(float delta) {
        if (stage != null) stage.act(delta);
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0.2f, 0.15f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (stage != null) stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (stage != null) stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (stage != null) stage.dispose();
    }
}
