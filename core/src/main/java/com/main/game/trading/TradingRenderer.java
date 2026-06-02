package com.main.game.trading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryLayout;
import com.main.game.inventory.ItemSlotRenderer;
import com.main.game.inventory.ItemStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TradingRenderer {

    private final ItemSlotRenderer itemSlotRenderer;
    private final Texture panelTexture;
    private final Texture highlightTexture;
    private final Map<String, Texture> offerTextures = new HashMap<>();

    public TradingRenderer() {
        itemSlotRenderer = new ItemSlotRenderer();
        panelTexture = new Texture(Gdx.files.internal("images/gui_invrow/villager_trading.png"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        highlightTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void renderTrading(SpriteBatch batch, Inventory inventory, TradingController controller,
                              float sw, float sh) {
        InventoryLayout.PanelRect panel = panel(sw, sh);

        batch.setColor(Color.WHITE);
        batch.draw(panelTexture, panel.x, panel.y, panel.width, panel.height);

        List<TradeOffer> offers = controller == null ? null : controller.getCurrentOffers();
        if (offers != null) {
            for (int i = 0; i < offers.size() && i < 3; i++) {
                drawOfferRow(batch, panel, offers.get(i), i,
                    controller.getSelectedOfferIndex() == i,
                    controller.getLastResult());
            }
        }

        TradeOffer selectedOffer = controller == null ? null : controller.getSelectedOffer();
        if (selectedOffer != null) {
            float previewSize = TradingLayout.previewItemSize(panel);
            float costSlotX = TradingLayout.costSlotX(panel);
            float resultSlotX = TradingLayout.resultSlotX(panel);
            itemSlotRenderer.draw(batch, selectedOffer.getPrimaryCost(),
                TradingLayout.previewItemX(panel, costSlotX),
                TradingLayout.previewItemY(panel),
                previewSize);
            itemSlotRenderer.draw(batch, selectedOffer.getResult(),
                TradingLayout.previewItemX(panel, resultSlotX),
                TradingLayout.previewItemY(panel),
                previewSize);
        }

        renderInventorySlots(batch, inventory, panel);
        batch.setColor(Color.WHITE);
    }

    public void renderCarriedStack(SpriteBatch batch, ItemStack stack) {
        itemSlotRenderer.renderCarriedStack(batch, stack);
    }

    public int findHoveredOffer(float screenX, float screenY, TradingController controller) {
        return TradingLayout.findOffer(screenX, screenY, panel(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()),
            controller == null ? 0 : controller.getCurrentOffers().size());
    }

    public int findHoveredInventorySlot(float screenX, float screenY) {
        return TradingLayout.findInventorySlot(screenX, screenY,
            panel(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public void dispose() {
        itemSlotRenderer.dispose();
        panelTexture.dispose();
        highlightTexture.dispose();
        for (Texture texture : offerTextures.values()) {
            texture.dispose();
        }
        offerTextures.clear();
    }

    private void drawOfferRow(SpriteBatch batch, InventoryLayout.PanelRect panel, TradeOffer offer,
                              int index, boolean selected, TradeResult lastResult) {
        if (selected) {
            if (lastResult == TradeResult.MISSING_COST || lastResult == TradeResult.NO_SPACE
                || lastResult == TradeResult.EXHAUSTED) {
                batch.setColor(0.9f, 0.18f, 0.12f, 0.24f);
            } else {
                batch.setColor(1f, 1f, 1f, 0.22f);
            }
            batch.draw(highlightTexture,
                TradingLayout.offerX(panel), TradingLayout.offerY(panel, index),
                TradingLayout.offerWidth(panel), TradingLayout.offerHeight(panel));
            batch.setColor(Color.WHITE);
        }

        Texture texture = textureForOffer(offer);
        if (texture != null) {
            batch.draw(texture,
                TradingLayout.offerIconX(panel), TradingLayout.offerIconY(panel, index),
                TradingLayout.offerIconWidth(panel), TradingLayout.offerIconHeight(panel));
        }
    }

    private void renderInventorySlots(SpriteBatch batch, Inventory inventory, InventoryLayout.PanelRect panel) {
        if (inventory == null) {
            return;
        }
        float slotSize = TradingLayout.slotSize(panel);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                itemSlotRenderer.drawInSlot(batch, inventory.getSlot(slotIndex),
                    TradingLayout.inventorySlotX(panel, col),
                    TradingLayout.inventoryMainSlotY(panel, row),
                    slotSize);
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            itemSlotRenderer.drawInSlot(batch, inventory.getSlot(col),
                TradingLayout.inventorySlotX(panel, col),
                TradingLayout.hotbarSlotY(panel),
                slotSize);
        }
    }

    private Texture textureForOffer(TradeOffer offer) {
        if (offer == null || offer.getTexturePath() == null) {
            return null;
        }
        String path = offer.getTexturePath();
        if (offerTextures.containsKey(path)) {
            return offerTextures.get(path);
        }

        FileHandle file = Gdx.files.internal(path);
        if (!file.exists() && !path.startsWith("image/")) {
            file = Gdx.files.internal("image/" + path);
        }
        if (!file.exists()) {
            offerTextures.put(path, null);
            return null;
        }

        Texture texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        offerTextures.put(path, texture);
        return texture;
    }

    private InventoryLayout.PanelRect panel(float sw, float sh) {
        return InventoryLayout.computePanel(sw, sh, panelTexture.getWidth(), panelTexture.getHeight());
    }
}
