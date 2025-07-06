package org.goober.linkmod.itemstuff;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class SeedBagItemCustom extends Item {
    private static final int SLOT_COUNT = 8;
    private static final int MAX_STACK_SIZE = 64;
    
    public SeedBagItemCustom(Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        
        if (!world.isClient && !contents.isEmpty()) {
            // Get the first non-empty seed stack
            ItemStack seedStack = null;
            int slotIndex = -1;
            for (int i = 0; i < contents.items().size(); i++) {
                ItemStack item = contents.get(i);
                if (!item.isEmpty()) {
                    seedStack = item;
                    slotIndex = i;
                    break;
                }
            }
            
            if (seedStack != null && slotIndex >= 0) {
                // Calculate how many seeds to throw
                int seedsToThrow = Math.min(seedStack.getCount(), 8);
                
                // Throw multiple seeds with variance
                for (int i = 0; i < seedsToThrow; i++) {
                    // Create and shoot the projectile
                    org.goober.linkmod.projectilestuff.SeedbagEntity projectile = new org.goober.linkmod.projectilestuff.SeedbagEntity(world, user, seedStack.copyWithCount(1));
                    
                    // Add slight variance to the velocity
                    float variance = 0.8F;
                    float pitchVariance = (world.getRandom().nextFloat() - 0.5F) * variance * 20;
                    float yawVariance = (world.getRandom().nextFloat() - 0.5F) * variance * 20;
                    
                    projectile.setVelocity(user, user.getPitch() + pitchVariance, user.getYaw() + yawVariance, 0.0F, 1.5F, 1.0F);
                    world.spawnEntity(projectile);
                }
                
                // Remove seeds from the inventory
                SeedBagContentsComponent.Builder builder = new SeedBagContentsComponent.Builder(contents);
                ItemStack remaining = seedStack.copy();
                remaining.decrement(seedsToThrow);
                builder.setStack(slotIndex, remaining);
                stack.set(LmodDataComponentTypes.SEEDBAG_CONTENTS, builder.build());
                
                // Play throw sound
                world.playSound(null, user.getX(), user.getY(), user.getZ(), 
                    SoundEvents.ENTITY_SNOWBALL_THROW, 
                    SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
                
                user.incrementStat(Stats.USED.getOrCreateStat(this));
                return ActionResult.SUCCESS;
            }
        }
        
        return ActionResult.PASS;
    }
    
    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.LEFT || !slot.canTakePartial(player)) {
            return false;
        }
        
        ItemStack otherStack = slot.getStack();
        if (otherStack.isEmpty()) {
            return false;
        }
        
        if (!isSeedItem(otherStack)) {
            return false;
        }
        
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        SeedBagContentsComponent.Builder builder = new SeedBagContentsComponent.Builder(contents);
        
        // Try to add the item to existing stacks first
        boolean added = false;
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack existing = builder.getStack(i);
            if (ItemStack.areItemsAndComponentsEqual(existing, otherStack)) {
                int transferAmount = Math.min(otherStack.getCount(), MAX_STACK_SIZE - existing.getCount());
                if (transferAmount > 0) {
                    existing.increment(transferAmount);
                    otherStack.decrement(transferAmount);
                    builder.setStack(i, existing);
                    added = true;
                    if (otherStack.isEmpty()) {
                        break;
                    }
                }
            }
        }
        
        // Try to add to empty slots
        if (!otherStack.isEmpty()) {
            for (int i = 0; i < SLOT_COUNT; i++) {
                if (builder.getStack(i).isEmpty()) {
                    int transferAmount = Math.min(otherStack.getCount(), MAX_STACK_SIZE);
                    builder.setStack(i, otherStack.split(transferAmount));
                    added = true;
                    if (otherStack.isEmpty()) {
                        break;
                    }
                }
            }
        }
        
        if (added) {
            stack.set(LmodDataComponentTypes.SEEDBAG_CONTENTS, builder.build());
            playInsertSound(player);
            return true;
        } else {
            playInsertFailSound(player);
            return false;
        }
    }
    
    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && otherStack.isEmpty() && slot.canTakePartial(player)) {
            // Remove the first item from the bag
            SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
            SeedBagContentsComponent.Builder builder = new SeedBagContentsComponent.Builder(contents);
            
            for (int i = 0; i < SLOT_COUNT; i++) {
                ItemStack item = builder.getStack(i);
                if (!item.isEmpty()) {
                    cursorStackReference.set(item.copy());
                    builder.setStack(i, ItemStack.EMPTY);
                    stack.set(LmodDataComponentTypes.SEEDBAG_CONTENTS, builder.build());
                    playRemoveOneSound(player);
                    return true;
                }
            }
        }
        return false;
    }
    
    // Custom tooltip method (without @Override since it might not exist in parent)
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        int totalSeeds = contents.getTotalCount();
        int usedSlots = 0;
        
        for (ItemStack item : contents.items()) {
            if (!item.isEmpty()) {
                usedSlots++;
            }
        }
        
        tooltip.add(Text.literal("Seeds: " + totalSeeds));
        tooltip.add(Text.literal("Slots: " + usedSlots + "/" + SLOT_COUNT));
    }
    
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        return !contents.isEmpty();
    }
    
    @Override
    public int getItemBarStep(ItemStack stack) {
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        int totalSeeds = contents.getTotalCount();
        int maxCapacity = SLOT_COUNT * MAX_STACK_SIZE;
        return Math.round(13.0F * (float)totalSeeds / (float)maxCapacity);
    }
    
    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x00FF00; // Green color for seeds
    }
    
    private static boolean isSeedItem(ItemStack stack) {
        return stack.isIn(net.minecraft.registry.tag.TagKey.of(net.minecraft.registry.RegistryKeys.ITEM, net.minecraft.util.Identifier.of("c", "seeds")))
                || stack.getItem() == Items.CARROT
                || stack.getItem() == Items.POTATO;
    }
    
    private static void playInsertSound(PlayerEntity player) {
        player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
    }
    
    private static void playInsertFailSound(PlayerEntity player) {
        player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }
    
    private static void playRemoveOneSound(PlayerEntity player) {
        player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
    }
}