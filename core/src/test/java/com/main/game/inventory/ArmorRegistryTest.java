package com.main.game.inventory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArmorRegistryTest {

    @Test
    public void armorDurabilityIsHighEnoughForRaidTesting() {
        assertEquals(90, ArmorRegistry.getMaxDurability("leather_cap"));
        assertEquals(80, ArmorRegistry.getMaxDurability("gold_helmet"));
        assertEquals(120, ArmorRegistry.getMaxDurability("copper_helmet"));
        assertEquals(160, ArmorRegistry.getMaxDurability("iron_helmet"));
        assertEquals(260, ArmorRegistry.getMaxDurability("diamond_helmet"));
        assertEquals(320, ArmorRegistry.getMaxDurability("netherite_helmet"));
    }

    @Test
    public void materialArmorPiecesShareDurability() {
        assertMaterialDurability("copper", 120);
        assertMaterialDurability("iron", 160);
        assertMaterialDurability("gold", 80);
        assertMaterialDurability("diamond", 260);
        assertMaterialDurability("netherite", 320);
    }

    private void assertMaterialDurability(String material, int expectedDurability) {
        assertEquals(expectedDurability, ArmorRegistry.getMaxDurability(material + "_helmet"));
        assertEquals(expectedDurability, ArmorRegistry.getMaxDurability(material + "_chestplate"));
        assertEquals(expectedDurability, ArmorRegistry.getMaxDurability(material + "_leggings"));
        assertEquals(expectedDurability, ArmorRegistry.getMaxDurability(material + "_boots"));
    }
}
