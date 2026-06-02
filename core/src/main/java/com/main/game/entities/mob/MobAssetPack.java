package com.main.game.entities.mob;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

final class MobAssetPack {

    private static final EnumMap<Mob.MobType, SharedAssets> CACHE = new EnumMap<>(Mob.MobType.class);

    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> hurtAnim;

    void load(Mob.MobType type) {
        SharedAssets sharedAssets = CACHE.get(type);
        if (sharedAssets == null) {
            sharedAssets = loadShared(type);
            CACHE.put(type, sharedAssets);
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

    private static SharedAssets loadShared(Mob.MobType type) {
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
            case COW:
            case DOG:
            case TAMED_HORSE:
            case HORSE:
            case WOLF:
            case CAT:
            case VILLAGER:
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
            case EVOKER:
                idleAnim = single(loadedTextures, "mobs/skeleton/mobs/skeletonface.png");
                walkAnim = sequenceWithFallback(loadedTextures, 0.12f,
                    "mobs/skeleton/mobs/skeleton1.png",
                    "mobs/skeleton/mobs/skeleton2.png",
                    "mobs/skeleton/mobs/skeleton3.png",
                    "mobs/skeleton/mobs/skeleton4.png");
                hurtAnim = single(loadedTextures, "mobs/skeleton/mobs/skeletonfacehurt.png");
                break;
            case VINDICATOR:
            case RAVAGER:
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
        if (!Gdx.files.internal(path).exists()) {
            return null;
        }
        Texture t = new Texture(Gdx.files.internal(path));
        loadedTextures.add(t);
        return t;
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
