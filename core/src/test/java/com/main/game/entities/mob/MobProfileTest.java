package com.main.game.entities.mob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MobProfileTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void pillagerUsesSkeletonRangedTimingWithHigherDamage() {
        MobProfile skeleton = MobProfile.forType(Mob.MobType.SKELETON);
        MobProfile pillager = MobProfile.forType(Mob.MobType.PILLAGER);

        assertEquals(skeleton.attackRange, pillager.attackRange, EPSILON);
        assertEquals(skeleton.attackCooldown, pillager.attackCooldown, EPSILON);
        assertEquals(skeleton.patrolRange, pillager.patrolRange, EPSILON);
        assertEquals(skeleton.chaseSpeed, pillager.chaseSpeed, EPSILON);
        assertTrue(pillager.attackDamage > skeleton.attackDamage);
        assertEquals(MobAttackStyle.RANGED, pillager.attackStyle);
        assertEquals(40f, pillager.renderPixelsPerTile, EPSILON);
    }

    @Test
    public void vindicatorIsFastHardHittingMeleeHostile() {
        MobProfile zombie = MobProfile.forType(Mob.MobType.ZOMBIE);
        MobProfile vindicator = MobProfile.forType(Mob.MobType.VINDICATOR);

        assertEquals(zombie.attackRange, vindicator.attackRange, EPSILON);
        assertEquals(1.5f, vindicator.attackCooldown, EPSILON);
        assertTrue(vindicator.chaseSpeed > zombie.chaseSpeed);
        assertTrue(vindicator.attackDamage > zombie.attackDamage);
        assertTrue(vindicator.maxHealth > zombie.maxHealth);
        assertEquals(MobAllegiance.HOSTILE, vindicator.allegiance);
        assertEquals(MobAttackStyle.MELEE, vindicator.attackStyle);
    }

    @Test
    public void evokerUsesCasterStyleWithPillagerDamageProjectile() {
        MobProfile evoker = MobProfile.forType(Mob.MobType.EVOKER);
        MobProfile pillager = MobProfile.forType(Mob.MobType.PILLAGER);

        assertEquals(MobAttackStyle.CASTER, evoker.attackStyle);
        assertEquals(pillager.attackDamage, evoker.attackDamage);
        assertEquals(6f, evoker.attackRange, EPSILON);
    }

    @Test
    public void vexIsWeakerFastMeleeIllager() {
        MobProfile vex = MobProfile.forType(Mob.MobType.VEX);
        MobProfile vindicator = MobProfile.forType(Mob.MobType.VINDICATOR);

        assertEquals(MobAllegiance.HOSTILE, vex.allegiance);
        assertEquals(MobAttackStyle.MELEE, vex.attackStyle);
        assertTrue(vex.attackDamage < vindicator.attackDamage);
        assertTrue(vex.maxHealth < vindicator.maxHealth);
        assertTrue(vex.chaseSpeed > vindicator.chaseSpeed);
        assertTrue(vex.width < vindicator.width);
        assertTrue(vex.height < vindicator.height);
        assertEquals(40f, vex.renderPixelsPerTile, EPSILON);
    }

    @Test
    public void pigAndSheepHaveRenderCapsForOversizedScratchSprites() {
        MobProfile pig = MobProfile.forType(Mob.MobType.PIG);
        MobProfile sheep = MobProfile.forType(Mob.MobType.SHEEP);

        assertEquals(1.35f, pig.maxRenderWidth, EPSILON);
        assertEquals(0.95f, pig.maxRenderHeight, EPSILON);
        assertEquals(1.3f, sheep.maxRenderWidth, EPSILON);
        assertEquals(1.25f, sheep.maxRenderHeight, EPSILON);
    }
}
