package org.goober.linkmod.itemstuff;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.tooltip.TooltipData;
import java.util.Optional;
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

import java.util.ArrayList;
import java.util.List;

public class SeedBagItemCustom extends Item {
    private static final int MAX_CAPACITY = 512; // 8 stacks worth
    
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
            for (ItemStack item : contents.items()) {
                if (!item.isEmpty()) {
                    seedStack = item;
                    break;
                }
            }
            
            if (seedStack != null) {
                // Calculate how many seeds to throw
                int seedsToThrow = Math.min(seedStack.getCount(), 8);
                
                // Create a new builder and remove seeds
                SeedBagContentsComponent.Builder builder = new SeedBagContentsComponent.Builder(contents);
                
                // Throw multiple seeds with variance
                for (int i = 0; i < seedsToThrow; i++) {
                    ItemStack thrownSeed = builder.removeOne();
                    if (!thrownSeed.isEmpty()) {
                        // Create and shoot the projectile
                        org.goober.linkmod.projectilestuff.SeedbagEntity projectile = new org.goober.linkmod.projectilestuff.SeedbagEntity(world, user, thrownSeed);
                        
                        // Add slight variance to the velocity
                        float variance = 0.8F;
                        float pitchVariance = (world.getRandom().nextFloat() - 0.5F) * variance * 20;
                        float yawVariance = (world.getRandom().nextFloat() - 0.5F) * variance * 20;
                        
                        projectile.setVelocity(user, user.getPitch() + pitchVariance, user.getYaw() + yawVariance, 0.0F, 1.5F, 1.0F);
                        world.spawnEntity(projectile);
                    }
                }
                
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
        if (clickType != ClickType.LEFT) {
            return false;
        }
        
        ItemStack otherStack = slot.getStack();
        if (otherStack.isEmpty() || !isSeedItem(otherStack)) {
            return false;
        }
        
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        SeedBagContentsComponent.Builder builder = new SeedBagContentsComponent.Builder(contents);
        
        int added = builder.add(otherStack);
        if (added > 0) {
            otherStack.decrement(added);
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
            // Remove full stack from the bag
            SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
            SeedBagContentsComponent.Builder builder = new SeedBagContentsComponent.Builder(contents);
            
            ItemStack removed = builder.removeFirst();
            if (!removed.isEmpty()) {
                cursorStackReference.set(removed);
                stack.set(LmodDataComponentTypes.SEEDBAG_CONTENTS, builder.build());
                playRemoveOneSound(player);
                return true;
            }
        } else if (clickType == ClickType.LEFT && !otherStack.isEmpty() && isSeedItem(otherStack)) {
            // Add items to the bag
            SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
            SeedBagContentsComponent.Builder builder = new SeedBagContentsComponent.Builder(contents);
            
            int added = builder.add(otherStack);
            if (added > 0) {
                otherStack.decrement(added);
                stack.set(LmodDataComponentTypes.SEEDBAG_CONTENTS, builder.build());
                playInsertSound(player);
                return true;
            } else {
                playInsertFailSound(player);
            }
        }
        return false;
    }
    
    // Custom tooltip method (without @Override since it might not exist in parent)
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        int totalSeeds = contents.getTotalCount();
        int uniqueTypes = contents.items().size();
        
        tooltip.add(Text.literal("Seeds: " + totalSeeds + "/" + MAX_CAPACITY));
        tooltip.add(Text.literal("Types: " + uniqueTypes));
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
        return Math.round(13.0F * (float)totalSeeds / (float)MAX_CAPACITY);
    }
    
    @Override
    public int getItemBarColor(ItemStack stack) {
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        int totalSeeds = contents.getTotalCount();
        
        // Red when full (512 items), teal otherwise
        if (totalSeeds >= MAX_CAPACITY) {
            return 0xFF0000; // Red
        } else {
            return 0x45818e; // Teal
        }
    }
    
    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        SeedBagContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY);
        // Show all items in the seed bag
        return Optional.of(new SeedBagTooltipData(new ArrayList<>(contents.items())));
    }
    
    private static boolean isSeedItem(ItemStack stack) {
        Item item = stack.getItem();
        // Check common vanilla seeds first
        return item == Items.WHEAT_SEEDS
                || item == Items.MELON_SEEDS
                || item == Items.PUMPKIN_SEEDS
                || item == Items.BEETROOT_SEEDS
                || item == Items.TORCHFLOWER_SEEDS
                || item == Items.PITCHER_POD
                || item == Items.CARROT
                || item == Items.POTATO
                || stack.isIn(net.minecraft.registry.tag.TagKey.of(net.minecraft.registry.RegistryKeys.ITEM, net.minecraft.util.Identifier.of("c", "seeds")));
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