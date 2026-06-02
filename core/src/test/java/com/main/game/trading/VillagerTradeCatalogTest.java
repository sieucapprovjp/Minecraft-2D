package com.main.game.trading;

import static org.junit.Assert.assertEquals;

import com.main.game.entities.mob.VillagerProfession;
import java.util.List;
import org.junit.Test;

public class VillagerTradeCatalogTest {

    @Test
    public void eachProfessionProvidesThreeTrades() {
        assertCatalog(VillagerProfession.UNEMPLOYED, "unemployed-2", "unemployed-3", "unemployed-5");
        assertCatalog(VillagerProfession.FARMER, "farmer-3", "farmer-4", "farmer-7");
        assertCatalog(VillagerProfession.BLACKSMITH, "blacksmith-1", "blacksmith-3", "blacksmith-5");
    }

    @Test
    public void pickedOffersAreCopies() {
        List<TradeOffer> first = VillagerTradeCatalog.pickOffers(VillagerProfession.FARMER, 3);
        List<TradeOffer> second = VillagerTradeCatalog.pickOffers(VillagerProfession.FARMER, 3);

        first.get(0).recordUse();

        assertEquals(1, first.get(0).getUses());
        assertEquals(0, second.get(0).getUses());
    }

    private void assertCatalog(VillagerProfession profession, String first, String second, String third) {
        List<TradeOffer> offers = VillagerTradeCatalog.pickOffers(profession, 3);

        assertEquals(3, offers.size());
        assertEquals(first, offers.get(0).getId());
        assertEquals(second, offers.get(1).getId());
        assertEquals(third, offers.get(2).getId());
        for (TradeOffer offer : offers) {
            assertEquals(profession, offer.getProfession());
            assertEquals("village_trading/" + offer.getId() + ".png", offer.getTexturePath());
        }
    }
}
