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

public class MenuScreen extends BaseScreen {

    private Stage stage;

    public MenuScreen(MainGame game) {
        super(game);
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.MENU;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("MINECRAFT 2D", UISkin.getSkin());
        titleLabel.setFontScale(3f);

        TextButton playBtn = new TextButton("Singleplayer", UISkin.getSkin());
        TextButton multiBtn = new TextButton("Multiplayer", UISkin.getSkin());
        TextButton optionsBtn = new TextButton("Options", UISkin.getSkin());
        TextButton quitBtn = new TextButton("Quit Game", UISkin.getSkin());

        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreenRouter().request(ScreenId.MODE_SELECT);
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(titleLabel).padBottom(80).row();
        table.add(playBtn).width(400).height(60).padBottom(20).row();
        table.add(multiBtn).width(400).height(60).padBottom(20).row();
        table.add(optionsBtn).width(400).height(60).padBottom(20).row();
        table.add(quitBtn).width(400).height(60).padBottom(20).row();
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
