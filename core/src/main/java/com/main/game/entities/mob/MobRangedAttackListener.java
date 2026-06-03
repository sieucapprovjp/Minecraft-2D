package com.main.game.entities.mob;

import com.main.game.entities.player.Player;

public interface MobRangedAttackListener {
    void onMobRangedAttack(Mob mob, Player target, int damage);
}
