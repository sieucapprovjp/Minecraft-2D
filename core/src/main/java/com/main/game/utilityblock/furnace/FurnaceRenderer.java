package com.main.game.utilityblock.furnace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryLayout;
import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemRenderOffset;
import com.main.game.inventory.ItemStack;

public class FurnaceRenderer {

    private static final float ITEM_SIZE_RATIO = 0.8125f;
    private static final float ITEM_Y_OFFSET_PX = -2f;
    private static final float ARROW_X = 362f;
    private static final float ARROW_Y = 514f;
    private static final float FLAME_X = 295f;
    private static final float FLAME_Y = 504f;
    private static final float FLAME_SCALE = 1.15f;
    private static final int MIN_BURNING_FLAME_FRAME = 1;

    private final BitmapFont font;
    private final Texture furnaceTexture;
    private final Texture[] arrowFrames = new Texture[12];
    private final Texture[] flameFrames = new Texture[14];
    private final Texture durabilityTexture;

    public FurnaceRenderer() {
        furnaceTexture = new Texture(Gdx.files.internal("util_block/gui/furnace.png"));
        for (int i = 0; i < arrowFrames.length; i++) {
            arrowFrames[i] = new Texture(Gdx.files.internal("util_block/process/arrow" + i + ".png"));
        }
        for (int i = 0; i < flameFrames.length; i++) {
            flameFrames[i] = new Texture(Gdx.files.internal("util_block/process/flame" + i + ".png"));
        }

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        durabilityTexture = new Texture(pixmap);
        pixmap.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/2c90030680a2fafd21f53fd39a0862e7.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 18;
        params.color = Color.WHITE;
        params.borderWidth = 2f;
        params.borderColor = new Color(0f, 0f, 0f, 0.85f);
        font = generator.generateFont(params);
        generator.dispose();
    }

    public void renderFurnace(SpriteBatch batch, Inventory inventory, FurnaceState furnaceState, float sw, float sh) {
        InventoryLayout.PanelRect panel = panel(sw, sh);

        batch.setColor(Color.WHITE);
        batch.draw(furnaceTexture, panel.x, panel.y, panel.width, panel.height);
        drawProgress(batch, panel, furnaceState);

        drawSlotItemInSlot(batch, furnaceState.getInput(),
            FurnaceLayout.inputSlotX(panel),
            FurnaceLayout.inputSlotY(panel),
            FurnaceLayout.slotSize(panel));
        drawSlotItemInSlot(batch, furnaceState.getFuel(),
            FurnaceLayout.fuelSlotX(panel),
            FurnaceLayout.fuelSlotY(panel),
            FurnaceLayout.slotSize(panel));
        drawSlotItemInSlot(batch, furnaceState.getOutput(),
            FurnaceLayout.outputSlotX(panel),
            FurnaceLayout.outputSlotY(panel),
            FurnaceLayout.slotSize(panel));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                drawSlotItemInSlot(batch, inventory.getSlot(slotIndex),
                    FurnaceLayout.inventorySlotX(panel, col),
                    FurnaceLayout.inventoryMainSlotY(panel, row),
                    FurnaceLayout.slotSize(panel));
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            drawSlotItemInSlot(batch, inventory.getSlot(col),
                FurnaceLayout.inventorySlotX(panel, col),
                FurnaceLayout.hotbarSlotY(panel),
                FurnaceLayout.slotSize(panel));
        }
    }

    public void renderCarriedStack(SpriteBatch batch, ItemStack stack) {
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float size = 42f;
        drawSlotItem(batch, stack, mouseX - size * 0.5f, mouseY - size * 0.5f, size);
    }

    public int findHoveredSlot(float screenX, float screenY) {
        return FurnaceLayout.findSlot(screenX, screenY, panel(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public void dispose() {
        font.dispose();
        furnaceTexture.dispose();
        for (Texture texture : arrowFrames) {
            texture.dispose();
        }
        for (Texture texture : flameFrames) {
            texture.dispose();
        }
        durabilityTexture.dispose();
    }

    private InventoryLayout.PanelRect panel(float sw, float sh) {
        return InventoryLayout.computePanel(sw, sh, furnaceTexture.getWidth(), furnaceTexture.getHeight());
    }

    private void drawProgress(SpriteBatch batch, InventoryLayout.PanelRect panel, FurnaceState furnaceState) {
        if (furnaceState == null) {
            return;
        }
        if (furnaceState.isBurning()) {
            int flameIndex = Math.max(MIN_BURNING_FLAME_FRAME,
                frameIndex(furnaceState.getBurnRatio(), flameFrames.length));
            drawProcessTexture(batch, flameFrames[flameIndex], panel, FLAME_X, FLAME_Y, FLAME_SCALE);
        }
        if (furnaceState.getCookRatio() > 0f) {
            int arrowIndex = frameIndex(furnaceState.getCookRatio(), arrowFrames.length);
            drawProcessTexture(batch, arrowFrames[arrowIndex], panel, ARROW_X, ARROW_Y, 1f);
        }
    }

    private int frameIndex(float ratio, int frameCount) {
        float clamped = Math.max(0f, Math.min(1f, ratio));
        return Math.min(frameCount - 1, (int) (clamped * (frameCount - 1)));
    }

    private void drawProcessTexture(SpriteBatch batch, Texture texture, InventoryLayout.PanelRect panel,
                                    float x, float y, float processScale) {
        float width = texture.getWidth() * panel.scale * processScale;
        float height = texture.getHeight() * panel.scale * processScale;
        float centerAdjustX = texture.getWidth() * panel.scale * (processScale - 1f) * 0.5f;
        batch.draw(texture,
            panel.x + x * panel.scale - centerAdjustX,
            panel.y + y * panel.scale,
            width,
            height);
    }

    private void drawSlotItemInSlot(SpriteBatch batch, ItemStack stack, float slotX, float slotY, float slotSize) {
        float itemSize = slotSize * ITEM_SIZE_RATIO;
        float itemX = slotX + (slotSize - itemSize) / 2f;
        float itemY = slotY + (slotSize - itemSize) / 2f + ITEM_Y_OFFSET_PX * (slotSize / InventoryLayout.INV_SLOT_SIZE_PX);
        drawSlotItem(batch, stack, itemX, itemY, itemSize);
    }

    private void drawSlotItem(SpriteBatch batch, ItemStack stack, float x, float y, float size) {
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        TextureRegion texture = ItemRegistry.getTexture(stack.getItemId());
        if (texture == null) {
            return;
        }
        float renderX = x + ItemRenderOffset.xOffset(stack.getItemId(), size);
        batch.draw(texture, renderX, y, size, size);
        drawDurabilityBar(batch, stack, renderX, y - size * 0.08f, size);
        if (stack.getCount() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, String.valueOf(stack.getCount()), renderX + size * 0.48f, y + size * 0.36f);
        }
    }

    private void drawDurabilityBar(SpriteBatch batch, ItemStack stack, float x, float y, float size) {
        if (!stack.hasDurability()) {
            return;
        }
        float ratio = stack.getDurabilityRatio();
        float barWidth = size * 0.82f;
        float barHeight = Math.max(2f, size * 0.07f);
        float barX = x + (size - barWidth) / 2f;
        float barY = y;

        batch.setColor(0f, 0f, 0f, 0.85f);
        batch.draw(durabilityTexture, barX - 1f, barY - 1f, barWidth + 2f, barHeight + 2f);

        if (ratio > 0.55f) {
            batch.setColor(0.15f, 0.9f, 0.25f, 1f);
        } else if (ratio > 0.25f) {
            batch.setColor(1f, 0.82f, 0.12f, 1f);
        } else {
            batch.setColor(0.95f, 0.16f, 0.12f, 1f);
        }
        batch.draw(durabilityTexture, barX, barY, barWidth * ratio, barHeight);
        batch.setColor(Color.WHITE);
    }
}
