package com.main.game.entities.mob;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class MobAssetPack {

    private static final Map<AssetKey, SharedAssets> CACHE = new HashMap<>();

    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> hurtAnim;

    void load(Mob.MobType type, VillagerProfession villagerProfession) {
        AssetKey key = AssetKey.of(type, villagerProfession);
        SharedAssets sharedAssets = CACHE.get(key);
        if (sharedAssets == null) {
            sharedAssets = loadShared(key.type, key.villagerProfession);
            CACHE.put(key, sharedAssets);
        }
        idleAnim = sharedAssets.idleAnim;
        walkAnim = sharedAssets.walkAnim;
        hurtAnim = sharedAssets.hurtAnim;
    }

    Animation<TextureRegion> idle() {
        return idleAnim;
    }

    Animation<TextureRegion> walk() {
        return walkAnim;
    }

    Animation<TextureRegion> hurt() {
        return hurtAnim;
    }

    void dispose() {
        // Shared mob textures are owned by the cache and disposed with GameScreen.
    }

    static void disposeSharedAssets() {
        for (SharedAssets assets : CACHE.values()) {
            assets.dispose();
        }
        CACHE.clear();
    }

    static int cachedMobTypeCount() {
        return CACHE.size();
    }

    static int loadedTextureCount() {
        int count = 0;
        for (SharedAssets assets : CACHE.values()) {
            count += assets.loadedTextures.size();
        }
        return count;
    }

    private static SharedAssets loadShared(Mob.MobType type, VillagerProfession villagerProfession) {
        List<Texture> loadedTextures = new ArrayList<>();
        Animation<TextureRegion> idleAnim;
        Animation<TextureRegion> walkAnim;
        Animation<TextureRegion> hurtAnim;

        switch (type) {
            case HUSK:
                idleAnim = single(loadedTextures, "mobs/husk/mobs/husk_face.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.11f,
                    "mobs/husk/mobs/husk_walk_2.png",
                    "mobs/husk/mobs/husk_walk_3.png",
                    "mobs/husk/mobs/husk_walk_4.png",
                    "mobs/husk/mobs/husk_walk_5.png",
                    "mobs/husk/mobs/husk_walk_6.png",
                    "mobs/husk/mobs/husk_walk_7.png",
                    "mobs/husk/mobs/husk_walk_8.png");
                hurtAnim = single(loadedTextures, "mobs/husk/mobs/husk_face.png");
                break;
            case SKELETON:
                idleAnim = single(loadedTextures, "mobs/skeleton/mobs/skeletonface.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.11f,
                    "mobs/skeleton/mobs/skeleton1.png",
                    "mobs/skeleton/mobs/skeleton2.png",
                    "mobs/skeleton/mobs/skeleton3.png",
                    "mobs/skeleton/mobs/skeleton4.png");
                hurtAnim = single(loadedTextures, "mobs/skeleton/mobs/skeletonfacehurt.png");
                break;
            case STRAY:
                idleAnim = single(loadedTextures, "mobs/skeleton/mobs/skeletonface.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.12f,
                    "mobs/skeleton/mobs/skeleton1.png",
                    "mobs/skeleton/mobs/skeleton2.png",
                    "mobs/skeleton/mobs/skeleton3.png",
                    "mobs/skeleton/mobs/skeleton4.png");
                hurtAnim = single(loadedTextures, "mobs/skeleton/mobs/skeletonfacehurt.png");
                break;
            case PIG:
                idleAnim = single(loadedTextures, "mobs/pig/mobs/piglook3.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.11f,
                    "mobs/pig/mobs/pig8.png",
                    "mobs/pig/mobs/pig9.png",
                    "mobs/pig/mobs/pig13.png",
                    "mobs/pig/mobs/pig14.png");
                hurtAnim = single(loadedTextures, "mobs/pig/mobs/pigdamage.png");
                break;
            case SHEEP:
                idleAnim = single(loadedTextures, "mobs/sheep/mobs/sheep_face.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.11f,
                    "mobs/sheep/mobs/sheep_2.png",
                    "mobs/sheep/mobs/sheep_3.png",
                    "mobs/sheep/mobs/sheep_4.png");
                hurtAnim = single(loadedTextures, "mobs/sheep/mobs/sheep_hurt.png");
                break;
            case CHICKEN:
                idleAnim = single(loadedTextures, "mobs/chicken/mobs/chickenface.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.10f,
                    "mobs/chicken/mobs/chicken1.png",
                    "mobs/chicken/mobs/chicken2.png",
                    "mobs/chicken/mobs/chicken3.png",
                    "mobs/chicken/mobs/chicken4.png",
                    "mobs/chicken/mobs/chicken5.png");
                hurtAnim = single(loadedTextures, "mobs/chicken/mobs/chickenface.png");
                break;
            case VILLAGER:
                switch (villagerProfession == null ? VillagerProfession.UNEMPLOYED : villagerProfession) {
                    case BLACKSMITH:
                        idleAnim = single(loadedTextures, "mobs/villager/blacksmith-look.png");
                        walkAnim = sequence(loadedTextures, "mobs/villager/blacksmith-walk%d.png", 1, 5, 0.12f);
                        hurtAnim = single(loadedTextures, "mobs/villager/blacksmith-hurt.png");
                        break;
                    case FARMER:
                        idleAnim = single(loadedTextures, "mobs/villager/farmer.png");
                        walkAnim = sequence(loadedTextures, "mobs/villager/farmer%d.png", 2, 6, 0.12f);
                        hurtAnim = single(loadedTextures, "mobs/villager/farmer-hurt.png");
                        break;
                    case UNEMPLOYED:
                    default:
                        idleAnim = single(loadedTextures, "mobs/villager/villager-face.png");
                        walkAnim = sequence(loadedTextures, "mobs/villager/villager-walk-%d.png", 1, 5, 0.12f);
                        hurtAnim = single(loadedTextures, "mobs/villager/villager_hurt.png");
                        break;
                }
                break;
            case COW:
            case DOG:
            case TAMED_HORSE:
            case HORSE:
            case WOLF:
            case CAT:
            case COD:
            case SALMON:
            case TROPICAL_FISH:
            case PUFFERFISH:
            case DOLPHIN:
                idleAnim = single(loadedTextures, "mvp/mob/cow/cow_look.png");
                walkAnim = sequence(loadedTextures, "mvp/mob/cow/cow_walk_%d.png", 1, 6, 0.12f);
                hurtAnim = single(loadedTextures, "mvp/mob/cow/cow_hurt.png");
                break;
            case PILLAGER:
                idleAnim = single(loadedTextures, "mobs/pillager/mobs/pillager_raid_captain_look.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.12f,
                    "mobs/pillager/mobs/pillager_raid_captain2.png",
                    "mobs/pillager/mobs/pillager_raid_captain3.png",
                    "mobs/pillager/mobs/pillager_raid_captain4.png",
                    "mobs/pillager/mobs/pillager_raid_captain5.png",
                    "mobs/pillager/mobs/pillager_raid_captain6.png");
                hurtAnim = single(loadedTextures, "mobs/pillager/mobs/pillager_raid_captain_hurt.png");
                break;
            case VINDICATOR:
                idleAnim = single(loadedTextures, "mobs/vindicator/mobs/vindicator_face.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.12f,
                    "mobs/vindicator/mobs/vindicator_walk_2.png",
                    "mobs/vindicator/mobs/vindicator_walk_3.png",
                    "mobs/vindicator/mobs/vindicator_walk_4.png",
                    "mobs/vindicator/mobs/vindicator_walk_5.png");
                hurtAnim = single(loadedTextures, "mobs/vindicator/mobs/vindicator_hurt_attack.png");
                break;
            case EVOKER:
                idleAnim = single(loadedTextures, "mobs/evoker/mobs/evoker-face2.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.12f,
                    "mobs/evoker/mobs/evoker_walk_2.png",
                    "mobs/evoker/mobs/evoker_walk_3.png",
                    "mobs/evoker/mobs/evoker_walk_4.png",
                    "mobs/evoker/mobs/evoker_walk_5.png");
                hurtAnim = single(loadedTextures, "mobs/evoker/mobs/evoker_hurt_attack.png");
                break;
            case RAVAGER:
                idleAnim = sequenceWithFallback(loadedTextures, 0.35f,
                    "mobs/ravager/mobs/ravager_idle.png",
                    "mobs/ravager/mobs/ravager_idle_2.png");
                walkAnim = sequence(loadedTextures, "mobs/ravager/mobs/ravager_walk_%d.png", 1, 6, 0.12f);
                hurtAnim = single(loadedTextures, "mobs/ravager/mobs/ravager_hurt.png");
                break;
            case ZOMBIE:
            default:
                idleAnim = single(loadedTextures, "mobs/zombie/mobs/zombielook.png");
                walkAnim = sequence(loadedTextures, "mobs/zombie/mobs/zombie%d.png", 4, 7, 0.11f);
                hurtAnim = single(loadedTextures, "mobs/zombie/mobs/zombievillager-hurt.png");
                break;
        }

        if (idleAnim == null) idleAnim = single(loadedTextures, "mvp/mob/cow/cow_look.png");
        if (walkAnim == null) walkAnim = sequence(loadedTextures, "mvp/mob/cow/cow_walk_%d.png", 1, 6, 0.12f);
        if (hurtAnim == null) hurtAnim = single(loadedTextures, "mvp/mob/cow/cow_hurt.png");
        return new SharedAssets(idleAnim, walkAnim, hurtAnim, loadedTextures);
    }

    private static Animation<TextureRegion> single(List<Texture> loadedTextures, String path) {
        Texture texture = loadTexture(loadedTextures, path);
        if (texture == null) return null;
        Animation<TextureRegion> anim = new Animation<>(0.6f, new TextureRegion(texture));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    private static Animation<TextureRegion> sequence(List<Texture> loadedTextures, String pathPattern,
                                                     int start, int endInclusive, float frameDur) {
        TextureRegion[] frames = new TextureRegion[endInclusive - start + 1];
        int count = 0;
        for (int i = start; i <= endInclusive; i++) {
            String path = String.format(pathPattern, i);
            Texture t = loadTexture(loadedTextures, path);
            if (t != null) {
                frames[count++] = new TextureRegion(t);
            }
        }
        if (count == 0) return null;
        TextureRegion[] trimmed = new TextureRegion[count];
        System.arraycopy(frames, 0, trimmed, 0, count);
        Animation<TextureRegion> anim = new Animation<>(frameDur, trimmed);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    private static Animation<TextureRegion> sequenceWithFallback(List<Texture> loadedTextures,
                                                                 float frameDur, String... paths) {
        TextureRegion[] frames = new TextureRegion[paths.length];
        int count = 0;
        for (String path : paths) {
            Texture t = loadTexture(loadedTextures, path);
            if (t != null) {
                frames[count++] = new TextureRegion(t);
            }
        }
        if (count == 0) return null;
        TextureRegion[] trimmed = new TextureRegion[count];
        System.arraycopy(frames, 0, trimmed, 0, count);
        Animation<TextureRegion> anim = new Animation<>(frameDur, trimmed);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    private static Texture loadTexture(List<Texture> loadedTextures, String path) {
        if (Gdx.files == null) {
            return null;
        }
        String resolvedPath = resolveInternalPath(path);
        if (resolvedPath == null) {
            return null;
        }
        Texture t = new Texture(Gdx.files.internal(resolvedPath));
        loadedTextures.add(t);
        return t;
    }

    private static String resolveInternalPath(String path) {
        if (path == null) {
            return null;
        }
        if (Gdx.files.internal(path).exists()) {
            return path;
        }
        String imagePath = path.startsWith("image/") ? path : "image/" + path;
        return Gdx.files.internal(imagePath).exists() ? imagePath : null;
    }

    private static final class AssetKey {
        final Mob.MobType type;
        final VillagerProfession villagerProfession;

        private AssetKey(Mob.MobType type, VillagerProfession villagerProfession) {
            this.type = type;
            this.villagerProfession = villagerProfession;
        }

        static AssetKey of(Mob.MobType type, VillagerProfession villagerProfession) {
            VillagerProfession normalizedProfession = type == Mob.MobType.VILLAGER
                ? (villagerProfession == null ? VillagerProfession.UNEMPLOYED : villagerProfession)
                : VillagerProfession.UNEMPLOYED;
            return new AssetKey(type, normalizedProfession);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof AssetKey)) return false;
            AssetKey other = (AssetKey) obj;
            return type == other.type && villagerProfession == other.villagerProfession;
        }

        @Override
        public int hashCode() {
            int result = type == null ? 0 : type.hashCode();
            result = 31 * result + (villagerProfession == null ? 0 : villagerProfession.hashCode());
            return result;
        }
    }

    private static final class SharedAssets {
        final Animation<TextureRegion> idleAnim;
        final Animation<TextureRegion> walkAnim;
        final Animation<TextureRegion> hurtAnim;
        final List<Texture> loadedTextures;

        SharedAssets(Animation<TextureRegion> idleAnim, Animation<TextureRegion> walkAnim,
                     Animation<TextureRegion> hurtAnim, List<Texture> loadedTextures) {
            this.idleAnim = idleAnim;
            this.walkAnim = walkAnim;
            this.hurtAnim = hurtAnim;
            this.loadedTextures = loadedTextures;
        }

        void dispose() {
            for (Texture texture : loadedTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
            loadedTextures.clear();
        }
    }
}
