package com.main.game.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.main.game.entities.Player;
import com.main.game.world.World;

public class DroppedItemManager {

    private final Array<DroppedItem> items = new Array<>();
    private float currentTime = 0f;

    public void spawn(HarvestEntry entry, World world) {
        if (entry != null && entry.getTexture() != null) {
            items.add(new DroppedItem(entry, world, currentTime));
        }
    }

    public void update(float delta, World world, Player player) {
        currentTime += delta;
        for (DroppedItem item : items) {
            item.update(delta, world, player);
        }
    }

    public void render(SpriteBatch batch) {
        for (DroppedItem item : items) {
            item.render(batch);
        }
    }

    public void clear() {
        items.clear();
    }
}
