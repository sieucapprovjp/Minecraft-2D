package com.main.game.audio;

import com.main.game.entities.mob.Mob;

public final class AudioCatalog {

    private static final String[] EMPTY = new String[0];
    private static final String[] STONE_BREAK = {
        "audio/block/stone1.mp3",
        "audio/block/stone2.mp3",
        "audio/block/stone3.mp3",
        "audio/block/stone4.mp3"
    };
    private static final String[] DEEPSLATE_BREAK = {
        "audio/block/deepslate1.mp3",
        "audio/block/deepslate2.mp3",
        "audio/block/deepslate3.mp3",
        "audio/block/deepslate4.mp3"
    };
    private static final String[] SAND_BREAK = {
        "audio/block/sand1.mp3",
        "audio/block/sand2.mp3",
        "audio/block/sand3.mp3",
        "audio/block/sand4.mp3"
    };
    private static final String[] SNOW_BREAK = {
        "audio/block/snow1.mp3",
        "audio/block/snow2.mp3",
        "audio/block/snow3.mp3",
        "audio/block/snow4.mp3"
    };
    private static final String[] WOOD_BREAK = {
        "audio/block/wood1.mp3",
        "audio/block/wood2.mp3",
        "audio/block/wood3.mp3",
        "audio/block/wood5.mp3"
    };
    private static final String[] PLAYER_HIT = {
        "audio/player/hit.mp3",
        "audio/player/hit2.mp3",
        "audio/player/hit3.mp3"
    };
    private static final String[] ZOMBIE_HURT = {
        "audio/mobs/zombiehurt1.wav",
        "audio/mobs/zombiehurt2.wav"
    };
    private static final String[] SKELETON_HURT = {
        "audio/mobs/skeletonhurt1.wav",
        "audio/mobs/skeletonhurt2.wav"
    };
    private static final String[] COW_HURT = {
        "audio/mobs/cowhurt1.wav",
        "audio/mobs/cowhurt2.wav",
        "audio/mobs/cowhurt3.wav"
    };
    private static final String[] CHICKEN_HURT = {
        "audio/mobs/chickenhurt1.wav",
        "audio/mobs/chickenhurt2.wav"
    };
    private static final String[] SHEEP_HURT = {
        "audio/mobs/sheephurt.wav"
    };

    private AudioCatalog() {
    }

    public static String[] soundPaths(AudioId id) {
        if (id == null) {
            return EMPTY;
        }
        switch (id) {
            case UI_CLICK:
            case UI_TOGGLE:
                return new String[] {"audio/ui_menu/click.mp3"};
            case PLAYER_HURT:
            case PLAYER_DEATH:
                return new String[] {"audio/player/hurt.mp3"};
            case PLAYER_EAT:
                return new String[] {"audio/player/eating.mp3"};
            case SWORD_SWING:
                return PLAYER_HIT;
            case CHEST_OPEN:
                return new String[] {"audio/chest_inventory/chestopen.mp3"};
            case CHEST_CLOSE:
                return new String[] {"audio/chest_inventory/chestclosed.mp3"};
            case ITEM_PICKUP:
                return new String[] {"audio/other/collect.mp3"};
            default:
                return EMPTY;
        }
    }

    public static String musicPath(AudioId id) {
        if (id == AudioId.MENU_MUSIC) {
            return "audio/ui_menu/music.mp3";
        }
        return null;
    }

    public static String[] blockBreakPaths(String blockId) {
        if (blockId == null) {
            return EMPTY;
        }
        String normalized = blockId.toLowerCase();
        if (normalized.contains("deepslate") || "ore_lapis_deepslate".equals(normalized)) {
            return DEEPSLATE_BREAK;
        }
        if (normalized.contains("sand")) {
            return SAND_BREAK;
        }
        if (normalized.contains("snow")) {
            return SNOW_BREAK;
        }
        if (normalized.contains("wood")
            || normalized.contains("log")
            || normalized.contains("planks")
            || "chest".equals(normalized)
            || "crafting_table".equals(normalized)) {
            return WOOD_BREAK;
        }
        if ("stone".equals(normalized) || normalized.endsWith("_ore") || normalized.contains("stone")) {
            return STONE_BREAK;
        }
        return EMPTY;
    }

    public static String[] mobHurtPaths(Mob.MobType type) {
        if (type == null) {
            return EMPTY;
        }
        switch (type) {
            case ZOMBIE:
            case HUSK:
                return ZOMBIE_HURT;
            case SKELETON:
            case STRAY:
                return SKELETON_HURT;
            case COW:
                return COW_HURT;
            case CHICKEN:
                return CHICKEN_HURT;
            case SHEEP:
                return SHEEP_HURT;
            default:
                return EMPTY;
        }
    }
}
