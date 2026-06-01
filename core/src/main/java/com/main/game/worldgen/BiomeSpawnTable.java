package com.main.game.worldgen;

import com.main.game.entities.mob.Mob;
import java.util.*;

/**
 * Quản lý weighted spawn table cho từng biome.
 * Đảm bảo weighted selection deterministic khi sử dùng cùng Random seed.
 *
 * Ví dụ:
 *  - FOREST: [COW(weight=20), PIG(weight=15), CHICKEN(weight=15), ZOMBIE(weight=5)]
 *  - DESERT: [HUSK(weight=15), SKELETON(weight=10)]
 *  - SNOW:   [STRAY(weight=12), SHEEP(weight=15)]
 */
public final class BiomeSpawnTable {

    private final Map<BiomeType, List<MobEntry>> biomeTables = new EnumMap<>(BiomeType.class);

    public BiomeSpawnTable() {
        initializeBiomes();
    }

    /**
     * Chọn mob type cho biome dựa trên weighted distribution.
     * Không phải random selection; dùng Random object để maintain seed consistency.
     *
     * @param biome BiomeType để spawn mob (null → FOREST fallback)
     * @param random Random instance (required)
     * @return MobType được chọn theo weighted distribution
     * @throws NullPointerException nếu random là null
     * @throws IllegalArgumentException nếu biome hoặc weights không hợp lệ
     */
    public Mob.MobType selectMobForBiome(BiomeType biome, Random random) {
        if (random == null) {
            throw new NullPointerException("Random instance cannot be null");
        }
        
        // Handle null biome → fallback FOREST
        if (biome == null) {
            biome = BiomeType.FOREST;
        }

        List<MobEntry> entries = biomeTables.get(biome);
        if (entries == null || entries.isEmpty()) {
            // Fallback: use FOREST if biome not configured
            entries = biomeTables.get(BiomeType.FOREST);
            if (entries == null || entries.isEmpty()) {
                return Mob.MobType.ZOMBIE; // Last resort
            }
        }

        // Tính tổng weight
        int totalWeight = 0;
        for (MobEntry e : entries) {
            if (e.weight < 0) {
                throw new IllegalArgumentException("Negative weight not allowed: " + e.type + " weight=" + e.weight);
            }
            totalWeight += e.weight;
        }

        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Total weight must be positive. Biome=" + biome + " total=" + totalWeight);
        }

        int pick = random.nextInt(totalWeight);
        int acc = 0;
        for (MobEntry e : entries) {
            acc += e.weight;
            if (pick < acc) {
                return e.type;
            }
        }
        return entries.get(0).type;
    }

    private void initializeBiomes() {
        // FOREST: nhiều passive, ít hostile
        List<MobEntry> forest = new ArrayList<>();
        forest.add(new MobEntry(Mob.MobType.COW, 20));
        forest.add(new MobEntry(Mob.MobType.PIG, 15));
        forest.add(new MobEntry(Mob.MobType.CHICKEN, 18));
        forest.add(new MobEntry(Mob.MobType.SHEEP, 12));
        forest.add(new MobEntry(Mob.MobType.ZOMBIE, 5));
        biomeTables.put(BiomeType.FOREST, forest);

        // DESERT: chỉ hostile
        List<MobEntry> desert = new ArrayList<>();
        desert.add(new MobEntry(Mob.MobType.HUSK, 15));
        desert.add(new MobEntry(Mob.MobType.SKELETON, 10));
        biomeTables.put(BiomeType.DESERT, desert);

        // SNOW: hostile + ít passive
        List<MobEntry> snow = new ArrayList<>();
        snow.add(new MobEntry(Mob.MobType.STRAY, 12));
        snow.add(new MobEntry(Mob.MobType.SHEEP, 15));
        biomeTables.put(BiomeType.SNOW, snow);
    }

    /**
     * Inner class lưu mob type + spawn weight.
     */
    private static final class MobEntry {
        final Mob.MobType type;
        final int weight;

        MobEntry(Mob.MobType type, int weight) {
            this.type = type;
            this.weight = weight;
        }
    }
}
