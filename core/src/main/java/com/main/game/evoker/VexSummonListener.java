package com.main.game.evoker;

import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;

public interface VexSummonListener {
    default boolean canSummonVex(Mob caster, Player target) {
        return true;
    }

    boolean onSummonVex(Mob caster, Player target);
}
