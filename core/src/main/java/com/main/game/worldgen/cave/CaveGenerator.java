package com.main.game.worldgen.cave;

import com.main.game.world.World;

public final class CaveGenerator {

    private CaveGenerator() {
    }

    public static void generate(World world, long seed) {
        if (world == null) return;
        CaveCarver.carve(world, seed);
        OreVeinPlacer.place(world, seed);
    }
}
