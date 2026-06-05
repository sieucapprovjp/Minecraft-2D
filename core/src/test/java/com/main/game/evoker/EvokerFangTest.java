package com.main.game.evoker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import com.badlogic.gdx.math.Rectangle;
import org.junit.Test;

public class EvokerFangTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void fangEmergesThenExpiresAfterHalfSecond() {
        EvokerFang fang = new EvokerFang(10f, 20f, 0f, 6);

        assertTrue(fang.isVisible());
        assertFalse(fang.isOpen());
        assertEquals(0.2f, fang.getScale(), EPSILON);

        fang.update(0.1f, null);

        assertTrue(fang.isOpen());
        assertEquals(0.36f, fang.getScale(), EPSILON);

        fang.update(0.4f, null);

        assertFalse(fang.isAlive());
    }

    @Test
    public void fangOnlyOverlapsForDamageAfterOpening() {
        EvokerFang fang = new EvokerFang(10f, 20f, 0f, 6);
        Rectangle target = new Rectangle(9.8f, 20f, 0.8f, 1.8f);

        assertFalse(fang.overlaps(target));

        fang.update(0.11f, null);

        assertTrue(fang.overlaps(target));
    }
}
