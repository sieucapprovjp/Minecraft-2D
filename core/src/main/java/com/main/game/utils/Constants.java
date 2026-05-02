package com.main.game.utils;

public final class Constants {

    private Constants() {}

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final String GAME_TITLE = "Paper Minecraft";

    public static final int TILE_SIZE = 32;
    public static final int WORLD_WIDTH = 400;
    public static final int WORLD_HEIGHT = 128;
    public static final int CHUNK_SIZE = 16;

    public static final float VIEWPORT_WIDTH_TILES = (float) SCREEN_WIDTH / TILE_SIZE;
    public static final float VIEWPORT_HEIGHT_TILES = (float) SCREEN_HEIGHT / TILE_SIZE;

    public static final float GRAVITY = -25f;
    public static final float TERMINAL_VELOCITY = -20f;

    public static final float PLAYER_SPEED = 8f;
    public static final float PLAYER_JUMP_FORCE = 12f;
    public static final float PLAYER_WIDTH = 0.9f;
    public static final float PLAYER_HEIGHT = 1.9f;

    public static final float COW_SPEED = 1.4f;
    public static final float COW_WIDTH = 1.6f;
    public static final float COW_HEIGHT = 1.2f;

    public static final String TEXTURE_ATLAS_PATH = "atlas/tiles.atlas";
    public static final String PLAYER_BODY_1_PATH = "organized_assets_en/images/steve/body2.png";
    public static final String PLAYER_BODY_2_PATH = "organized_assets_en/images/steve/body4.png";
    public static final String[] PLAYER_WALK_PATHS = {
        "organized_assets_en/images/steve/body_cp_2.png",
        "organized_assets_en/images/steve/body_cp_3.png",
        "organized_assets_en/images/steve/body_cp_5.png",
        "organized_assets_en/images/steve/body_cp_7.png",
        "organized_assets_en/images/steve/body_cp_8.png",
        "organized_assets_en/images/steve/body_cp_9.png",
        "organized_assets_en/images/steve/body_cp_10.png",
        "organized_assets_en/images/steve/body_cp_11.png",
        "organized_assets_en/images/steve/body_cp_12.png",
        "organized_assets_en/images/steve/body_cp_13.png",
        "organized_assets_en/images/steve/body_cp_14.png"
    };
    public static final String COW_IDLE_PATH = "organized_assets_en/images/mobs/cowlook.png";
    public static final String COW_HURT_PATH = "organized_assets_en/images/mobs/cow-hurt.png";
    public static final String[] COW_WALK_PATHS = {
        "organized_assets_en/images/mobs/cow1.png",
        "organized_assets_en/images/mobs/cow2.png",
        "organized_assets_en/images/mobs/cow3.png",
        "organized_assets_en/images/mobs/cow4.png",
        "organized_assets_en/images/mobs/cow5.png",
        "organized_assets_en/images/mobs/cow6.png"
    };

    public static final int LAYER_BACKGROUND = 0;
    public static final int LAYER_WORLD = 1;
    public static final int LAYER_ENTITY = 2;
    public static final int LAYER_UI = 3;
}
