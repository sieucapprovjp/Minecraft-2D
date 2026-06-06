package com.main.game.utilityblock.jukebox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.blocks.AbstractBlock;
import com.main.game.entities.player.Player;
import com.main.game.inventory.MusicDiscRegistry;
import com.main.game.world.World;

public class JukeboxInteractionController {

    public static final String JUKEBOX_ID = "jukebox";

    private static final float PLAY_REACH = 4.5f;

    private final Vector2 mouseWorld = new Vector2();
    private final JukeboxManager jukeboxManager;

    public JukeboxInteractionController(JukeboxManager jukeboxManager) {
        this.jukeboxManager = jukeboxManager == null ? new JukeboxManager() : jukeboxManager;
    }

    public InteractionResult update(Player player, World world, OrthographicCamera camera, Viewport viewport,
                                    String heldItemId, boolean inputBlocked) {
        if (inputBlocked || !Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            return null;
        }
        if (player == null
            || world == null
            || camera == null
            || viewport == null
            || !player.isAlive()) {
            return null;
        }

        camera.update();
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);

        int tileX = (int) Math.floor(mouseWorld.x);
        int tileY = (int) Math.floor(mouseWorld.y);
        AbstractBlock block = world.getBlock(tileX, tileY);
        if (block == null
            || !JUKEBOX_ID.equals(block.getBlockId())
            || !isWithinReach(player, tileX, tileY)) {
            return null;
        }
        return interact(world, tileX, tileY, heldItemId);
    }

    public InteractionResult interact(World world, int tileX, int tileY, String heldItemId) {
        if (jukeboxManager.hasDisc(world, tileX, tileY)) {
            String discItemId = jukeboxManager.removeDisc(world, tileX, tileY);
            return createResult(InteractionType.EJECT, tileX, tileY, discItemId);
        }
        if (!MusicDiscRegistry.isMusicDisc(heldItemId)
            || !jukeboxManager.insertDisc(world, tileX, tileY, heldItemId)) {
            return null;
        }
        return createResult(InteractionType.INSERT, tileX, tileY, heldItemId);
    }

    public static InteractionResult createInsertRequest(String blockId, int tileX, int tileY, String heldItemId) {
        if (!JUKEBOX_ID.equals(blockId)) {
            return null;
        }
        return createResult(InteractionType.INSERT, tileX, tileY, heldItemId);
    }

    private static InteractionResult createResult(InteractionType type, int tileX, int tileY, String discItemId) {
        MusicDiscRegistry.MusicDiscDefinition disc = MusicDiscRegistry.get(discItemId);
        if (disc == null) {
            return null;
        }
        return new InteractionResult(type, tileX, tileY, disc.getItemId(), disc.getDisplayName(), disc.getMusicPath());
    }

    private boolean isWithinReach(Player player, int tileX, int tileY) {
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;
        float tileCenterX = tileX + 0.5f;
        float tileCenterY = tileY + 0.5f;
        float dx = tileCenterX - playerCenterX;
        float dy = tileCenterY - playerCenterY;
        return dx * dx + dy * dy <= PLAY_REACH * PLAY_REACH;
    }

    public enum InteractionType {
        INSERT,
        EJECT
    }

    public static final class InteractionResult {
        private final InteractionType type;
        private final int tileX;
        private final int tileY;
        private final String discItemId;
        private final String displayName;
        private final String musicPath;

        private InteractionResult(InteractionType type, int tileX, int tileY,
                                  String discItemId, String displayName, String musicPath) {
            this.type = type;
            this.tileX = tileX;
            this.tileY = tileY;
            this.discItemId = discItemId;
            this.displayName = displayName;
            this.musicPath = musicPath;
        }

        public InteractionType getType() {
            return type;
        }

        public int getTileX() {
            return tileX;
        }

        public int getTileY() {
            return tileY;
        }

        public String getDiscItemId() {
            return discItemId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getMusicPath() {
            return musicPath;
        }
    }
}
