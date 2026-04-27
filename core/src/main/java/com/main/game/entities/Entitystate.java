package com.main.game.entities;

/**
 * State machine cơ bản cho mọi entity.
 * Dùng chung cho Player, Mob, Animal...
 */
public enum EntityState {
    IDLE,
    RUN,
    JUMP,
    FALL,
    HURT,
    DEAD
}
