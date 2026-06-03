package com.main.game.trading;

import com.main.game.entities.mob.VillagerProfession;
import com.main.game.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TradeOffer {

    private final String id;
    private final VillagerProfession profession;
    private final List<ItemStack> costs;
    private final ItemStack result;
    private final String texturePath;
    private final int maxUses;
    private int uses;

    public TradeOffer(String id, VillagerProfession profession, ItemStack cost,
                      ItemStack result, String texturePath, int maxUses) {
        this(id, profession, Collections.singletonList(cost), result, texturePath, maxUses, 0);
    }

    private TradeOffer(String id, VillagerProfession profession, List<ItemStack> costs,
                       ItemStack result, String texturePath, int maxUses, int uses) {
        this.id = id;
        this.profession = profession;
        this.costs = copyStacks(costs);
        this.result = result == null ? null : result.copy();
        this.texturePath = texturePath;
        this.maxUses = maxUses;
        this.uses = uses;
    }

    public String getId() {
        return id;
    }

    public VillagerProfession getProfession() {
        return profession;
    }

    public List<ItemStack> getCosts() {
        return copyStacks(costs);
    }

    public ItemStack getPrimaryCost() {
        return costs.isEmpty() ? null : costs.get(0).copy();
    }

    public ItemStack getResult() {
        return result == null ? null : result.copy();
    }

    public String getTexturePath() {
        return texturePath;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getUses() {
        return uses;
    }

    public boolean canUse() {
        return maxUses <= 0 || uses < maxUses;
    }

    public TradeOffer copy() {
        return new TradeOffer(id, profession, costs, result, texturePath, maxUses, uses);
    }

    void recordUse() {
        uses++;
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        List<ItemStack> copies = new ArrayList<>();
        if (stacks == null) {
            return copies;
        }
        for (ItemStack stack : stacks) {
            if (stack != null && stack.getCount() > 0) {
                copies.add(stack.copy());
            }
        }
        return Collections.unmodifiableList(copies);
    }
}
