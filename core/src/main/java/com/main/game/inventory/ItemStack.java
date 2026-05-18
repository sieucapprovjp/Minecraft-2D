package com.main.game.inventory;

public class ItemStack {

    private final String itemId;
    private int count;

    public ItemStack(String itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }

    public String getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void add(int amount) {
        count += amount;
    }

    public void subtract(int amount) {
        count -= amount;
    }

    public ItemStack copy() {
        return new ItemStack(itemId, count);
    }
}
