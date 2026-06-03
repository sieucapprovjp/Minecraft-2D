package com.main.game.trading;

import com.main.game.entities.mob.Mob;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.ItemStack;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class TradingController {

    private static final int OFFERS_PER_VILLAGER = 3;

    private final Map<Mob, List<TradeOffer>> offersByVillager = new IdentityHashMap<>();
    private Mob currentVillager;
    private List<TradeOffer> currentOffers = Collections.emptyList();
    private int selectedOfferIndex;
    private TradeResult lastResult = TradeResult.SUCCESS;

    public boolean open(Mob villager) {
        if (villager == null || villager.getType() != Mob.MobType.VILLAGER || !villager.isAlive()) {
            return false;
        }
        currentVillager = villager;
        currentOffers = offersByVillager.computeIfAbsent(villager,
            key -> VillagerTradeCatalog.pickOffers(key.getVillagerProfession(), OFFERS_PER_VILLAGER));
        selectedOfferIndex = currentOffers.isEmpty() ? -1 : 0;
        lastResult = TradeResult.SUCCESS;
        return true;
    }

    public void close() {
        currentVillager = null;
        currentOffers = Collections.emptyList();
        selectedOfferIndex = 0;
        lastResult = TradeResult.SUCCESS;
    }

    public boolean isOpen() {
        return currentVillager != null;
    }

    public Mob getCurrentVillager() {
        return currentVillager;
    }

    public List<TradeOffer> getCurrentOffers() {
        return currentOffers;
    }

    public int getSelectedOfferIndex() {
        return selectedOfferIndex;
    }

    public void selectOffer(int index) {
        if (index >= 0 && index < currentOffers.size()) {
            selectedOfferIndex = index;
        }
    }

    public TradeOffer getSelectedOffer() {
        if (selectedOfferIndex < 0 || selectedOfferIndex >= currentOffers.size()) {
            return null;
        }
        return currentOffers.get(selectedOfferIndex);
    }

    public TradeResult executeSelected(Inventory inventory) {
        return executeOffer(selectedOfferIndex, inventory);
    }

    public TradeResult executeOffer(int index, Inventory inventory) {
        if (inventory == null || index < 0 || index >= currentOffers.size()) {
            lastResult = TradeResult.INVALID_OFFER;
            return lastResult;
        }

        TradeOffer offer = currentOffers.get(index);
        if (!offer.canUse()) {
            lastResult = TradeResult.EXHAUSTED;
            return lastResult;
        }
        if (!hasCosts(inventory, offer)) {
            lastResult = TradeResult.MISSING_COST;
            return lastResult;
        }

        ItemStack result = offer.getResult();
        if (!inventory.canAdd(result)) {
            lastResult = TradeResult.NO_SPACE;
            return lastResult;
        }

        for (ItemStack cost : offer.getCosts()) {
            inventory.remove(cost.getItemId(), cost.getCount());
        }
        ItemStack leftover = inventory.addStack(result);
        if (leftover != null) {
            lastResult = TradeResult.NO_SPACE;
            return lastResult;
        }
        offer.recordUse();
        selectedOfferIndex = index;
        lastResult = TradeResult.SUCCESS;
        return lastResult;
    }

    public TradeResult getLastResult() {
        return lastResult;
    }

    private boolean hasCosts(Inventory inventory, TradeOffer offer) {
        for (ItemStack cost : offer.getCosts()) {
            if (!inventory.hasItems(cost.getItemId(), cost.getCount())) {
                return false;
            }
        }
        return true;
    }
}
