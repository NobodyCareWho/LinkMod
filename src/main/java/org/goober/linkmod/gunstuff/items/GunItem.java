package org.goober.linkmod.gunstuff.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.goober.linkmod.gunstuff.GunContentsComponent;
import org.goober.linkmod.gunstuff.GunTooltipData;
import org.goober.linkmod.itemstuff.LmodDataComponentTypes;
import org.goober.linkmod.projectilestuff.BulletEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GunItem extends Item {
    private static final int MAX_CAPACITY = 6; // 6 bullets
    private final String gunTypeId;
    
    public GunItem(Settings settings, String gunTypeId) {
        super(settings);
        this.gunTypeId = gunTypeId;
    }
    
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        
        System.out.println("Gun use called - isEmpty: " + contents.isEmpty());
        
        if (!world.isClient && !contents.isEmpty()) {
            try {
                // check cooldown
                if (user.getItemCooldownManager().isCoolingDown(stack)) {
                    return ActionResult.FAIL;
                }
                
                // get the first bullet
                GunContentsComponent.Builder builder = new GunContentsComponent.Builder(contents);
                ItemStack bulletStack = builder.removeOne();
                
                if (!bulletStack.isEmpty()) {
                    System.out.println("Shooting bullet: " + bulletStack);
                    Guns.GunType gunType = Guns.get(gunTypeId);
                    
                    // shoot multiple bullets for shotgun
                    for (int i = 0; i < gunType.pelletsPerShot(); i++) {
                        // create and shoot the bullet
                        BulletEntity bullet = new BulletEntity(world, user, bulletStack);
                        bullet.setDamage(gunType.damage());
                        
                        // set velocity with spread based on gun type
                        float spread = gunType.pelletsPerShot() > 1 ? 6.0F : 1.0F;
                        bullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, gunType.velocity(), spread);
                        world.spawnEntity(bullet);
                        System.out.println("Spawned bullet entity");
                    }
                    
                    // update gun contents
                    stack.set(LmodDataComponentTypes.GUN_CONTENTS, builder.build());
                    
                    // play gun sound
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), 
                        SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, 
                        SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
                    
                    // add cooldown
                    user.getItemCooldownManager().set(stack, gunType.fireRate());
                    
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    return ActionResult.SUCCESS;
                }
            } catch (Exception e) {
                System.err.println("Error shooting gun: " + e.getMessage());
                e.printStackTrace();
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
        if (otherStack.isEmpty() || !isBulletItem(otherStack) || !isCompatibleBullet(otherStack)) {
            return false;
        }
        
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        GunContentsComponent.Builder builder = new GunContentsComponent.Builder(contents);
        
        int added = builder.add(otherStack);
        if (added > 0) {
            otherStack.decrement(added);
            stack.set(LmodDataComponentTypes.GUN_CONTENTS, builder.build());
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
            // remove full stack from the gun
            GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
            GunContentsComponent.Builder builder = new GunContentsComponent.Builder(contents);
            
            ItemStack removed = builder.removeFirst();
            if (!removed.isEmpty()) {
                cursorStackReference.set(removed);
                stack.set(LmodDataComponentTypes.GUN_CONTENTS, builder.build());
                playRemoveOneSound(player);
                return true;
            }
        } else if (clickType == ClickType.LEFT && !otherStack.isEmpty() && isBulletItem(otherStack) && isCompatibleBullet(otherStack)) {
            // add bullets to the gun
            GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
            GunContentsComponent.Builder builder = new GunContentsComponent.Builder(contents);
            
            int added = builder.add(otherStack);
            if (added > 0) {
                otherStack.decrement(added);
                stack.set(LmodDataComponentTypes.GUN_CONTENTS, builder.build());
                playInsertSound(player);
                return true;
            } else {
                playInsertFailSound(player);
            }
        }
        return false;
    }
    
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        int totalBullets = contents.getTotalCount();
        
        Guns.GunType gunType = Guns.get(gunTypeId);
        tooltip.add(Text.literal("Gun Type: " + gunType.displayName()));
        tooltip.add(Text.literal("Damage: " + gunType.damage()));
        tooltip.add(Text.literal("Fire Rate: " + (60.0f / gunType.fireRate()) + " shots/sec"));
        tooltip.add(Text.literal("Ammo: " + totalBullets + "/" + MAX_CAPACITY));
        tooltip.add(Text.literal("Accepts: " + String.join(", ", gunType.acceptedAmmoTypes())));
    }
    
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true; // always show ammo bar
    }
    
    @Override
    public int getItemBarStep(ItemStack stack) {
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        int totalBullets = contents.getTotalCount();
        return Math.round(13.0F * (float)totalBullets / (float)MAX_CAPACITY);
    }
    
    @Override
    public int getItemBarColor(ItemStack stack) {
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        int totalBullets = contents.getTotalCount();
        
        // red when empty, yellow when low, green when full
        if (totalBullets == 0) {
            return 0xFF0000; // red
        } else {
            return 0x00FF00; // green
        }
    }
    
    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        // show all bullets in the gun
        return Optional.of(new GunTooltipData(new ArrayList<>(contents.items())));
    }
    
    private static boolean isBulletItem(ItemStack stack) {
        // check if item is a bullet
        return stack.getItem() instanceof BulletItem;
    }
    
    private boolean isCompatibleBullet(ItemStack bulletStack) {
        // check if this gun accepts this bullet type
        if (!(bulletStack.getItem() instanceof BulletItem bulletItem)) {
            return false;
        }
        
        Guns.GunType gunType = Guns.get(gunTypeId);
        return gunType.acceptsAmmo(bulletItem.getBulletTypeId());
    }
    
    public String getGunTypeId() {
        return gunTypeId;
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