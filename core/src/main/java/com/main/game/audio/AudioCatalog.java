package com.main.game.audio;

import com.main.game.entities.mob.Mob;

public final class AudioCatalog {

    private static final String[] EMPTY = new String[0];
    private static final String[] STONE_BREAK = {
        "audio/block/stone1.ogg",
        "audio/block/stone2.ogg",
        "audio/block/stone3.ogg",
        "audio/block/stone4.ogg"
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
    private static final String[] FURNACE_CRACKLE = {
        "audio/chest_inventory/furnace/fire_crackle1.ogg",
        "audio/chest_inventory/furnace/fire_crackle2.ogg",
        "audio/chest_inventory/furnace/fire_crackle3.ogg",
        "audio/chest_inventory/furnace/fire_crackle4.ogg",
        "audio/chest_inventory/furnace/fire_crackle5.ogg"
    };
    private static final String[] INGAME_MUSIC = {
        "audio/ui_menu/ingame_music/a_familiar_room.ogg",
        "audio/ui_menu/ingame_music/below_and_above.ogg",
        "audio/ui_menu/ingame_music/deeper.ogg",
        "audio/ui_menu/ingame_music/minecraft.ogg",
        "audio/ui_menu/ingame_music/sweden.ogg",
        "audio/ui_menu/ingame_music/yakusoku.ogg"
    };
    private static final String[] ZOMBIE_HURT = {
        "audio/mobs/zombie/zombiehurt1.wav",
        "audio/mobs/zombie/zombiehurt2.wav"
    };
    private static final String[] SKELETON_HURT = {
        "audio/mobs/skeleton/hurt1.ogg",
        "audio/mobs/skeleton/hurt2.ogg",
        "audio/mobs/skeleton/hurt3.ogg",
        "audio/mobs/skeleton/hurt4.ogg"
    };
    private static final String[] HUSK_HURT = {
        "audio/mobs/husk/hurt1.ogg",
        "audio/mobs/husk/hurt2.ogg"
    };
    private static final String[] COW_HURT = {
        "audio/mobs/cow/cowhurt1.wav",
        "audio/mobs/cow/cowhurt2.wav",
        "audio/mobs/cow/cowhurt3.wav"
    };
    private static final String[] CHICKEN_HURT = {
        "audio/mobs/chicken/chickenhurt1.wav",
        "audio/mobs/chicken/chickenhurt2.wav"
    };
    private static final String[] SHEEP_HURT = {
        "audio/mobs/sheep/sheep1.ogg",
        "audio/mobs/sheep/sheep2.ogg"
    };
    private static final String[] ZOMBIE_DEATH = {
        "audio/mobs/zombie/zombiedeath.wav"
    };
    private static final String[] SKELETON_DEATH = {
        "audio/mobs/skeleton/death.ogg"
    };
    private static final String[] HUSK_DEATH = {
        "audio/mobs/husk/death1.ogg",
        "audio/mobs/husk/death2.ogg"
    };
    private static final String[] SHEEP_DEATH = {
        "audio/mobs/sheep/sheep_dead.ogg"
    };
    private static final String[] EVOKER_IDLE = {
        "audio/mobs/evoker/idle1.mp3",
        "audio/mobs/evoker/idle2.mp3"
    };
    private static final String[] EVOKER_HURT = {
        "audio/mobs/evoker/hurt1.mp3"
    };
    private static final String[] EVOKER_DEATH = {
        "audio/mobs/evoker/death1.mp3"
    };
    private static final String[] EVOKER_CELEBRATE = {
        "audio/mobs/evoker/celebrate.mp3"
    };
    private static final String[] PILLAGER_IDLE = {
        "audio/mobs/pillager/idle1.ogg",
        "audio/mobs/pillager/idle2.ogg",
        "audio/mobs/pillager/idle3.ogg",
        "audio/mobs/pillager/idle4.ogg"
    };
    private static final String[] PILLAGER_HURT = {
        "audio/mobs/pillager/hurt1.ogg",
        "audio/mobs/pillager/hurt2.ogg",
        "audio/mobs/pillager/hurt3.ogg"
    };
    private static final String[] PILLAGER_DEATH = {
        "audio/mobs/pillager/death1.ogg",
        "audio/mobs/pillager/death2.ogg"
    };
    private static final String[] PILLAGER_CELEBRATE = {
        "audio/mobs/pillager/celebrate1.ogg",
        "audio/mobs/pillager/celebrate2.ogg",
        "audio/mobs/pillager/celebrate3.ogg",
        "audio/mobs/pillager/celebrate4.ogg"
    };
    private static final String[] PILLAGER_HORN_CELEBRATE = {
        "audio/mobs/pillager/horn_celebrate.ogg"
    };
    private static final String[] VINDICATOR_IDLE = {
        "audio/mobs/vindicator/idle1.ogg",
        "audio/mobs/vindicator/idle2.ogg",
        "audio/mobs/vindicator/idle3.ogg",
        "audio/mobs/vindicator/idle4.ogg",
        "audio/mobs/vindicator/idle5.ogg"
    };
    private static final String[] VINDICATOR_HURT = {
        "audio/mobs/vindicator/hurt1.ogg",
        "audio/mobs/vindicator/hurt2.ogg",
        "audio/mobs/vindicator/hurt3.ogg"
    };
    private static final String[] VINDICATOR_DEATH = {
        "audio/mobs/vindicator/death1.ogg"
    };
    private static final String[] VINDICATOR_CELEBRATE = {
        "audio/mobs/vindicator/celebrate1.ogg"
    };
    private static final String[] RAVAGER_IDLE = {
        "audio/mobs/ravager/idle1.ogg",
        "audio/mobs/ravager/idle2.ogg",
        "audio/mobs/ravager/idle3.ogg",
        "audio/mobs/ravager/idle4.ogg",
        "audio/mobs/ravager/idle5.ogg",
        "audio/mobs/ravager/idle6.ogg",
        "audio/mobs/ravager/idle7.ogg",
        "audio/mobs/ravager/idle8.ogg"
    };
    private static final String[] RAVAGER_HURT = {
        "audio/mobs/ravager/hurt1.ogg",
        "audio/mobs/ravager/hurt2.ogg",
        "audio/mobs/ravager/hurt3.ogg",
        "audio/mobs/ravager/hurt4.ogg"
    };
    private static final String[] RAVAGER_DEATH = {
        "audio/mobs/ravager/death1.ogg",
        "audio/mobs/ravager/death2.ogg",
        "audio/mobs/ravager/death3.ogg"
    };
    private static final String[] RAVAGER_STEP = {
        "audio/mobs/ravager/step1.ogg",
        "audio/mobs/ravager/step2.ogg",
        "audio/mobs/ravager/step3.ogg",
        "audio/mobs/ravager/step4.ogg",
        "audio/mobs/ravager/step5.ogg"
    };
    private static final String[] RAVAGER_BITE = {
        "audio/mobs/ravager/bite1.ogg",
        "audio/mobs/ravager/bite2.ogg",
        "audio/mobs/ravager/bite3.ogg"
    };
    private static final String[] VEX_IDLE = {
        "audio/mobs/vex/idle1.ogg",
        "audio/mobs/vex/idle2.ogg",
        "audio/mobs/vex/idle3.ogg",
        "audio/mobs/vex/idle4.ogg"
    };
    private static final String[] VEX_HURT = {
        "audio/mobs/vex/hurt1.ogg",
        "audio/mobs/vex/hurt2.ogg"
    };
    private static final String[] VEX_DEATH = {
        "audio/mobs/vex/death1.ogg",
        "audio/mobs/vex/death2.ogg"
    };
    private static final String[] VEX_CHARGE = {
        "audio/mobs/vex/charge1.ogg",
        "audio/mobs/vex/charge2.ogg",
        "audio/mobs/vex/charge3.ogg"
    };
    private static final String[] RAVAGER_CELEBRATE = {
        "audio/mobs/ravager/celebrate1.ogg",
        "audio/mobs/ravager/celebrate2.ogg"
    };
    private static final String[] RAID_CELEBRATE = concat(
        PILLAGER_CELEBRATE,
        VINDICATOR_CELEBRATE,
        RAVAGER_CELEBRATE,
        EVOKER_CELEBRATE
    );

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
            case UTILITY_BLOCK_OPEN:
                return new String[] {"audio/chest_inventory/door/open_door.mp3"};
            case UTILITY_BLOCK_CLOSE:
                return new String[] {"audio/chest_inventory/door/close_door.mp3"};
            case FURNACE_CRACKLE:
                return FURNACE_CRACKLE;
            case ITEM_PICKUP:
                return new String[] {"audio/other/collect.mp3"};
            case EVOKER_CAST:
                return new String[] {"audio/mobs/evoker/cast2.mp3"};
            case EVOKER_FANGS:
                return new String[] {"audio/mobs/evoker/fangs.mp3"};
            case EVOKER_IDLE:
                return EVOKER_IDLE;
            case RAID_WAVE_HORN:
                return PILLAGER_HORN_CELEBRATE;
            case RAID_CELEBRATE:
                return RAID_CELEBRATE;
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

    public static String[] ingameMusicPaths() {
        return INGAME_MUSIC.clone();
    }

    public static String raidMusicPath() {
        return "audio/ui_menu/rubedo.ogg";
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
                return ZOMBIE_HURT;
            case HUSK:
                return HUSK_HURT;
            case SKELETON:
            case STRAY:
                return SKELETON_HURT;
            case COW:
                return COW_HURT;
            case CHICKEN:
                return CHICKEN_HURT;
            case SHEEP:
                return SHEEP_HURT;
            case EVOKER:
                return EVOKER_HURT;
            case PILLAGER:
                return PILLAGER_HURT;
            case VINDICATOR:
                return VINDICATOR_HURT;
            case RAVAGER:
                return RAVAGER_HURT;
            case VEX:
                return VEX_HURT;
            default:
                return EMPTY;
        }
    }

    public static String[] mobDeathPaths(Mob.MobType type) {
        if (type == null) {
            return EMPTY;
        }
        if (type == Mob.MobType.ZOMBIE) {
            return ZOMBIE_DEATH;
        }
        if (type == Mob.MobType.HUSK) {
            return HUSK_DEATH;
        }
        if (type == Mob.MobType.SKELETON || type == Mob.MobType.STRAY) {
            return SKELETON_DEATH;
        }
        if (type == Mob.MobType.SHEEP) {
            return SHEEP_DEATH;
        }
        if (type == Mob.MobType.EVOKER) {
            return EVOKER_DEATH;
        }
        if (type == Mob.MobType.PILLAGER) {
            return PILLAGER_DEATH;
        }
        if (type == Mob.MobType.VINDICATOR) {
            return VINDICATOR_DEATH;
        }
        if (type == Mob.MobType.RAVAGER) {
            return RAVAGER_DEATH;
        }
        if (type == Mob.MobType.VEX) {
            return VEX_DEATH;
        }
        return mobHurtPaths(type);
    }

    public static String[] mobIdlePaths(Mob.MobType type) {
        if (type == null) {
            return EMPTY;
        }
        switch (type) {
            case EVOKER:
                return EVOKER_IDLE;
            case PILLAGER:
                return PILLAGER_IDLE;
            case VINDICATOR:
                return VINDICATOR_IDLE;
            case RAVAGER:
                return RAVAGER_IDLE;
            case VEX:
                return VEX_IDLE;
            default:
                return EMPTY;
        }
    }

    public static String[] mobStepPaths(Mob.MobType type) {
        if (type == Mob.MobType.RAVAGER) {
            return RAVAGER_STEP;
        }
        return EMPTY;
    }

    public static String[] mobAttackPaths(Mob.MobType type) {
        if (type == null) {
            return EMPTY;
        }
        if (type == Mob.MobType.RAVAGER) {
            return RAVAGER_BITE;
        }
        if (type == Mob.MobType.VEX) {
            return VEX_CHARGE;
        }
        if (type == Mob.MobType.PILLAGER || type == Mob.MobType.VINDICATOR || type == Mob.MobType.EVOKER) {
            return mobIdlePaths(type);
        }
        return EMPTY;
    }

    public static boolean hasMobAmbientPaths(Mob.MobType type) {
        return mobIdlePaths(type).length > 0 || mobStepPaths(type).length > 0;
    }

    private static String[] concat(String[]... groups) {
        int length = 0;
        for (String[] group : groups) {
            if (group != null) {
                length += group.length;
            }
        }
        String[] merged = new String[length];
        int index = 0;
        for (String[] group : groups) {
            if (group == null) {
                continue;
            }
            System.arraycopy(group, 0, merged, index, group.length);
            index += group.length;
        }
        return merged;
    }
}
