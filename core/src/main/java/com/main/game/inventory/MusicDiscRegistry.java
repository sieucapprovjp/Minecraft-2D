package com.main.game.inventory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class MusicDiscRegistry {

    private static final Map<String, MusicDiscDefinition> DISCS = new LinkedHashMap<>();

    static {
        register("pigstep", "Pigstep", "audio/ui_menu/disc/pigstep.ogg", "pigstep");
        register("lava_chicken", "N\u00e0 N\u00e1 Na Na Anh \u0110\u1ed9 Mixi",
            "audio/ui_menu/disc/N\u00e0 N\u00e1 Na Na Anh \u0110\u1ed9 Mixi.mp3", "lava_chicken");
    }

    private MusicDiscRegistry() {
    }

    public static boolean isMusicDisc(String itemId) {
        return itemId != null && DISCS.containsKey(itemId);
    }

    public static MusicDiscDefinition get(String itemId) {
        return itemId == null ? null : DISCS.get(itemId);
    }

    public static Set<String> getMusicDiscItemIds() {
        return Collections.unmodifiableSet(DISCS.keySet());
    }

    private static void register(String itemId, String displayName, String musicPath, String textureName) {
        DISCS.put(itemId, new MusicDiscDefinition(itemId, displayName, musicPath, textureName));
    }

    public static final class MusicDiscDefinition {
        private final String itemId;
        private final String displayName;
        private final String musicPath;
        private final String textureName;

        private MusicDiscDefinition(String itemId, String displayName, String musicPath, String textureName) {
            this.itemId = itemId;
            this.displayName = displayName;
            this.musicPath = musicPath;
            this.textureName = textureName;
        }

        public String getItemId() {
            return itemId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getMusicPath() {
            return musicPath;
        }

        public String getTextureName() {
            return textureName;
        }
    }
}
