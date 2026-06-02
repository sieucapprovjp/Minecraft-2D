package com.main.game.raid;

import com.badlogic.gdx.Gdx;
import com.main.game.world.World;
import com.main.game.worldgen.village.VillageState;

public final class RaidController {

    public static final String RAID_BANNER_BLOCK_ID = "raid_banner";

    private RaidState state = RaidState.IDLE;
    private int triggerTileX = -1;
    private int triggerTileY = -1;

    public boolean tryStartFromBanner(World world, String blockId, int tileX, int tileY) {
        if (!RAID_BANNER_BLOCK_ID.equals(blockId) || state != RaidState.IDLE || world == null) {
            return false;
        }
        VillageState village = world.getVillageState();
        if (village == null || !village.containsRaidBannerTile(tileX, tileY)) {
            return false;
        }

        state = RaidState.COUNTDOWN;
        triggerTileX = tileX;
        triggerTileY = tileY;
        if (Gdx.app != null) {
            Gdx.app.log("RaidController", "Raid started from banner at " + tileX + "," + tileY);
        }
        return true;
    }

    public RaidState getState() {
        return state;
    }

    public int getTriggerTileX() {
        return triggerTileX;
    }

    public int getTriggerTileY() {
        return triggerTileY;
    }
}
