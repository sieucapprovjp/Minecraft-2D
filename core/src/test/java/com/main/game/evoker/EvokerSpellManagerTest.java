package com.main.game.evoker;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import org.junit.Test;

public class EvokerSpellManagerTest {

    @Test
    public void choosesBetweenAllAvailableSpellsWithoutPriority() {
        EvokerSpellManager manager = new EvokerSpellManager(new ScriptedRandom(0, 1, 2));

        assertEquals(EvokerSpellManager.CastResult.SUMMON_VEX,
            manager.chooseSpell(true, true, true));
        assertEquals(EvokerSpellManager.CastResult.FANGS,
            manager.chooseSpell(true, true, true));
        assertEquals(EvokerSpellManager.CastResult.PROJECTILE,
            manager.chooseSpell(true, true, true));
    }

    @Test
    public void skipsSummonWhenVexCapIsReached() {
        EvokerSpellManager manager = new EvokerSpellManager(new ScriptedRandom(0, 1));

        assertEquals(EvokerSpellManager.CastResult.FANGS,
            manager.chooseSpell(false, true, true));
        assertEquals(EvokerSpellManager.CastResult.PROJECTILE,
            manager.chooseSpell(false, true, true));
    }

    private static final class ScriptedRandom extends Random {
        private final int[] values;
        private int index;

        private ScriptedRandom(int... values) {
            super(0L);
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            int value = values[index++];
            if (value < 0 || value >= bound) {
                throw new AssertionError("Scripted value " + value + " is outside bound " + bound);
            }
            return value;
        }
    }
}
