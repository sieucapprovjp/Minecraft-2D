package com.main.game.trading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.main.game.entities.mob.Mob;
import com.main.game.entities.mob.VillagerProfession;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.ItemStack;
import org.junit.Test;

public class TradingControllerTest {

    @Test
    public void tradesWhenInventoryHasCostAndSpace() {
        TradingController controller = new TradingController();
        Inventory inventory = new Inventory();
        inventory.add("coal", 15);

        assertTrue(controller.open(villager(VillagerProfession.BLACKSMITH)));
        controller.selectOffer(1);

        assertEquals(TradeResult.SUCCESS, controller.executeSelected(inventory));
        assertEquals(0, inventory.countItem("coal"));
        assertEquals(1, inventory.countItem("emerald"));
        assertEquals(1, controller.getSelectedOffer().getUses());
    }

    @Test
    public void missingCostDoesNotChangeInventory() {
        TradingController controller = new TradingController();
        Inventory inventory = new Inventory();
        inventory.add("coal", 14);

        assertTrue(controller.open(villager(VillagerProfession.BLACKSMITH)));
        controller.selectOffer(1);

        assertEquals(TradeResult.MISSING_COST, controller.executeSelected(inventory));
        assertEquals(14, inventory.countItem("coal"));
        assertEquals(0, inventory.countItem("emerald"));
    }

    @Test
    public void fullInventoryDoesNotRemoveCostForDurableResult() {
        TradingController controller = new TradingController();
        Inventory inventory = new Inventory();
        inventory.setSlot(0, new ItemStack("emerald", 10));
        for (int i = 1; i < Inventory.TOTAL_SIZE; i++) {
            inventory.setSlot(i, new ItemStack("dirt", 1));
        }

        assertTrue(controller.open(villager(VillagerProfession.BLACKSMITH)));
        controller.selectOffer(0);

        assertEquals(TradeResult.NO_SPACE, controller.executeSelected(inventory));
        assertEquals(10, inventory.countItem("emerald"));
        assertEquals(0, inventory.countItem("iron_chestplate"));
    }

    @Test
    public void villagerKeepsRuntimeOfferState() {
        TradingController controller = new TradingController();
        Mob villager = villager(VillagerProfession.FARMER);
        Inventory inventory = new Inventory();
        inventory.add("emerald", 3);

        assertTrue(controller.open(villager));
        controller.selectOffer(0);
        assertEquals(TradeResult.SUCCESS, controller.executeSelected(inventory));
        controller.close();

        assertTrue(controller.open(villager));
        assertEquals(1, controller.getCurrentOffers().get(0).getUses());
    }

    private Mob villager(VillagerProfession profession) {
        return new Mob(0f, 0f, Mob.MobType.VILLAGER, profession, null, null, null);
    }
}
