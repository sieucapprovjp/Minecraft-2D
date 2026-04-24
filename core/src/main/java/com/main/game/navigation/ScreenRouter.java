package com.main.game.navigation;

import com.badlogic.gdx.Screen;
import com.main.game.MainGame;
import com.main.game.screens.BaseScreen;

public class ScreenRouter {

    private final MainGame game;
    private ScreenId pending;

    public ScreenRouter(MainGame game) {
        this.game = game;
    }

    public void request(ScreenId next) {
        if (next == null) {
            return;
        }

        Screen current = game.getScreen();
        if (pending == null && current instanceof BaseScreen) {
            if (((BaseScreen) current).getScreenId() == next) {
                return;
            }
        }
        this.pending = next;
    }

    public void flush() {
        if (pending == null) {
            return;
        }

        ScreenId target = pending;
        pending = null;

        Screen current = game.getScreen();
        if (current instanceof BaseScreen) {
            ((BaseScreen) current).onExit();
        }
        if (current != null) {
            current.dispose();
        }

        Screen next = game.createScreen(target);
        game.setScreen(next);
        if (next instanceof BaseScreen) {
            ((BaseScreen) next).onEnter();
        }
    }
}
