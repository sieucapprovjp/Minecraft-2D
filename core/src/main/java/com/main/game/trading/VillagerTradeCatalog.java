package com.main.game.trading;

import com.main.game.entities.mob.VillagerProfession;
import com.main.game.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class VillagerTradeCatalog {

    private static final int DEFAULT_MAX_USES = 16;
    private static final Map<VillagerProfession, List<TradeOffer>> OFFERS =
        new EnumMap<>(VillagerProfession.class);

    static {
        register(VillagerProfession.UNEMPLOYED,
            offer("unemployed-2", VillagerProfession.UNEMPLOYED, "emerald", 1, "bread", 6),
            offer("unemployed-3", VillagerProfession.UNEMPLOYED, "rotten_flesh", 32, "emerald", 1),
            offer("unemployed-5", VillagerProfession.UNEMPLOYED, "emerald", 1, "cake", 1));

        register(VillagerProfession.FARMER,
            offer("farmer-3", VillagerProfession.FARMER, "emerald", 3, "cookie", 18),
            offer("farmer-4", VillagerProfession.FARMER, "emerald", 2, "bread", 3),
            offer("farmer-7", VillagerProfession.FARMER, "emerald", 1, "apple", 4));

        register(VillagerProfession.BLACKSMITH,
            offer("blacksmith-1", VillagerProfession.BLACKSMITH, "emerald", 10, "iron_chestplate", 1),
            offer("blacksmith-3", VillagerProfession.BLACKSMITH, "coal", 15, "emerald", 1),
            offer("blacksmith-5", VillagerProfession.BLACKSMITH, "emerald", 4, "iron_axe", 1));
    }

    private VillagerTradeCatalog() {
    }

    public static List<TradeOffer> pickOffers(VillagerProfession profession, int limit) {
        List<TradeOffer> templates = OFFERS.getOrDefault(normalize(profession),
            OFFERS.get(VillagerProfession.UNEMPLOYED));
        if (templates == null || templates.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }

        int count = Math.min(limit, templates.size());
        List<TradeOffer> picked = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            picked.add(templates.get(i).copy());
        }
        return Collections.unmodifiableList(picked);
    }

    public static List<TradeOffer> allFor(VillagerProfession profession) {
        return pickOffers(profession, Integer.MAX_VALUE);
    }

    private static VillagerProfession normalize(VillagerProfession profession) {
        return profession == null ? VillagerProfession.UNEMPLOYED : profession;
    }

    private static void register(VillagerProfession profession, TradeOffer... offers) {
        List<TradeOffer> list = new ArrayList<>();
        for (TradeOffer offer : offers) {
            list.add(offer);
        }
        OFFERS.put(profession, Collections.unmodifiableList(list));
    }

    private static TradeOffer offer(String id, VillagerProfession profession,
                                    String costItem, int costCount,
                                    String resultItem, int resultCount) {
        return new TradeOffer(id, profession,
            new ItemStack(costItem, costCount),
            new ItemStack(resultItem, resultCount),
            "village_trading/" + id + ".png",
            DEFAULT_MAX_USES);
    }
}
