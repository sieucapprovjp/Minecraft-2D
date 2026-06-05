package com.main.game.entities.mob;

import com.main.game.entities.player.Player;

public interface MobCastSpellListener {
    void onMobCastSpell(Mob mob, Player target, int damage);
}
