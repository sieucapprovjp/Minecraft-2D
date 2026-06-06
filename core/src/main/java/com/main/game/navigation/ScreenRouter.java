package com.main.game.navigation;

import com.badlogic.gdx.Screen;
import com.main.game.MainGame;
import com.main.game.screens.BaseScreen;

public class ScreenRouter {

    private final MainGame game;
    private ScreenId pending;
    private Screen suspendedGameScreen;
    private boolean pendingHelpReturn;

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

    public void requestHelpReturn() {
        pendingHelpReturn = true;
    }

    public void flush() {
        if (pendingHelpReturn) {
            pendingHelpReturn = false;
            Screen current = game.getScreen();
            if (current instanceof BaseScreen) {
                ((BaseScreen) current).onExit();
            }
            if (current != null) {
                current.dispose();
            }
            if (suspendedGameScreen != null) {
                Screen restore = suspendedGameScreen;
                suspendedGameScreen = null;
                game.setScreen(restore);
                if (restore instanceof BaseScreen) {
                    ((BaseScreen) restore).onEnter();
                }
            } else {
                request(ScreenId.MENU);
            }
            return;
        }

        if (pending == null) {
            return;
        }

        ScreenId target = pending;
        pending = null;

        Screen current = game.getScreen();
        if (target == ScreenId.HELP
            && current instanceof BaseScreen
            && ((BaseScreen) current).getScreenId() == ScreenId.GAME) {
            suspendedGameScreen = current;
            Screen next = game.createScreen(target);
            game.setScreen(next);
            if (next instanceof BaseScreen) {
                ((BaseScreen) next).onEnter();
            }
            return;
        }

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
