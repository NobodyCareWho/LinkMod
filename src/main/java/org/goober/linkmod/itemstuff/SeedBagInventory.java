package org.goober.linkmod.itemstuff;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class SeedBagInventory implements Inventory {
    private static final int SLOT_COUNT = 8;
    private static final int MAX_STACK_SIZE = 64;
    private final DefaultedList<ItemStack> items;
    
    public SeedBagInventory() {
        this.items = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
    }
    
    @Override
    public int size() {
        return SLOT_COUNT;
    }
    
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = items.get(slot);
        if (!stack.isEmpty()) {
            ItemStack result = stack.split(amount);
            if (stack.isEmpty()) {
                items.set(slot, ItemStack.EMPTY);
            }
            markDirty();
            return result;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = items.get(slot);
        if (!stack.isEmpty()) {
            items.set(slot, ItemStack.EMPTY);
            markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > getMaxCount(stack)) {
            stack.setCount(getMaxCount(stack));
        }
        markDirty();
    }
    
    @Override
    public int getMaxCount(ItemStack stack) {
        return MAX_STACK_SIZE;
    }
    
    @Override
    public void markDirty() {
        // To be implemented when we add persistence
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public void clear() {
        items.clear();
    }
    
    // Custom method to check if an item can be inserted
    public boolean canInsert(ItemStack stack) {
        return isSeedItem(stack);
    }
    
    private static boolean isSeedItem(ItemStack stack) {
        return stack.isIn(net.minecraft.registry.tag.TagKey.of(net.minecraft.registry.RegistryKeys.ITEM, net.minecraft.util.Identifier.of("c", "seeds")))
                || stack.getItem() == Items.CARROT
                || stack.getItem() == Items.POTATO;
    }
    
    // Add item to the first available slot
    public boolean addItem(ItemStack stack) {
        if (!canInsert(stack)) {
            return false;
        }
        
        // First try to merge with existing stacks
        for (int i = 0; i < size(); i++) {
            ItemStack existing = getStack(i);
            if (ItemStack.areItemsAndComponentsEqual(existing, stack)) {
                int transferAmount = Math.min(stack.getCount(), getMaxCount(stack) - existing.getCount());
                if (transferAmount > 0) {
                    existing.increment(transferAmount);
                    stack.decrement(transferAmount);
                    markDirty();
                    if (stack.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        
        // Then try to find an empty slot
        for (int i = 0; i < size(); i++) {
            if (getStack(i).isEmpty()) {
                setStack(i, stack.split(Math.min(stack.getCount(), getMaxCount(stack))));
                return stack.isEmpty();
            }
        }
        
        return false;
    }
    
    public DefaultedList<ItemStack> getItems() {
        return items;
    }
}