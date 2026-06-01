package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

final class StageBackgrounds {

    private static final String[] STAGE_DIRS = {
        "stage",
        "image/stage"
    };
    private static final String[] KNOWN_STAGE_FILES = {
        "cherry_blossom.png",
        "corals.png",
        "lush_caves.png",
        "sculk.png",
        "sky.png",
        "stronghold.png",
        "trial_chamber.png",
        "warped_forest.png"
    };

    private StageBackgrounds() {
    }

    static FileHandle random() {
        for (String dir : STAGE_DIRS) {
            FileHandle[] files = imageFiles(Gdx.files.internal(dir).list());
            if (files.length > 0) {
                return files[MathUtils.random(files.length - 1)];
            }
        }

        for (String dir : STAGE_DIRS) {
            FileHandle file = randomKnownFile(dir);
            if (file != null) {
                return file;
            }
        }
        return Gdx.files.internal("images/stage_sprite/empty2.png");
    }

    private static FileHandle[] imageFiles(FileHandle[] files) {
        if (files == null || files.length == 0) {
            return new FileHandle[0];
        }
        int count = 0;
        for (FileHandle file : files) {
            if (isImage(file)) {
                count++;
            }
        }
        FileHandle[] images = new FileHandle[count];
        int index = 0;
        for (FileHandle file : files) {
            if (isImage(file)) {
                images[index++] = file;
            }
        }
        return images;
    }

    private static boolean isImage(FileHandle file) {
        if (file == null || file.isDirectory()) {
            return false;
        }
        String name = file.name().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

    private static FileHandle randomKnownFile(String dir) {
        int start = MathUtils.random(KNOWN_STAGE_FILES.length - 1);
        for (int i = 0; i < KNOWN_STAGE_FILES.length; i++) {
            String name = KNOWN_STAGE_FILES[(start + i) % KNOWN_STAGE_FILES.length];
            FileHandle file = Gdx.files.internal(dir + "/" + name);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
}
