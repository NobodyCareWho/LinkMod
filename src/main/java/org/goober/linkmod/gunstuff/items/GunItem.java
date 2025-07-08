package org.goober.linkmod.gunstuff.items;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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
                
                // find and remove first compatible bullet
                GunContentsComponent.Builder builder = new GunContentsComponent.Builder(contents);
                Guns.GunType gunType = Guns.get(gunTypeId);
                
                // try to remove a compatible bullet
                ItemStack bulletStack = ItemStack.EMPTY;
                List<ItemStack> items = new ArrayList<>(contents.items());
                for (int i = 0; i < items.size(); i++) {
                    ItemStack item = items.get(i);
                    if (item.getItem() instanceof BulletItem bulletItem) {
                        // check if this bullet type is compatible with this gun
                        if (gunType.acceptsAmmo(bulletItem.getBulletTypeId())) {
                            // found a compatible bullet, remove one
                            bulletStack = item.split(1);
                            if (item.isEmpty()) {
                                items.remove(i);
                            }
                            // rebuild the component
                            builder = new GunContentsComponent.Builder();
                            for (ItemStack remainingStack : items) {
                                builder.add(remainingStack);
                            }
                            break;
                        }
                    }
                }
                
                if (!bulletStack.isEmpty()) {
                    System.out.println("Shooting bullet: " + bulletStack);
                    
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
                    
                    // handle shell ejection based on mode
                    if (gunType.ejectsShells()) {
                        String shellItemId = gunType.ejectShellItemId();
                        Item shellItem = Registries.ITEM.get(Identifier.of("lmod", shellItemId));
                        if (shellItem != null && shellItem != Items.AIR) {
                            ItemStack emptyShell = new ItemStack(shellItem, 1);
                            
                            switch (gunType.shellEjectionMode()) {
                                case TO_BUNDLE -> {
                                    // add empty shell back to gun's bundle
                                    builder.add(emptyShell);
                                }
                                case TO_WORLD -> {
                                    // drop empty shell as item entity to the right
                                    Vec3d lookDirection = user.getRotationVector();
                                    Vec3d rightVector = new Vec3d(-lookDirection.z, 0, lookDirection.x).normalize();
                                    
                                    // spawn shell to the right with some velocity
                                    ItemEntity shellEntity = new ItemEntity(
                                        world,
                                        user.getX() + rightVector.x * 0.5,
                                        user.getY() + 0.5,
                                        user.getZ() + rightVector.z * 0.5,
                                        emptyShell
                                    );
                                    
                                    // add velocity to eject shell to the right
                                    shellEntity.setVelocity(
                                        rightVector.x * 0.2 + world.random.nextGaussian() * 0.05,
                                        0.2 + world.random.nextGaussian() * 0.05,
                                        rightVector.z * 0.2 + world.random.nextGaussian() * 0.05
                                    );
                                    
                                    // set pickup delay to 2 seconds (40 ticks)
                                    shellEntity.setPickupDelay(40);
                                    
                                    world.spawnEntity(shellEntity);
                                }
                            }
                        }
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
        Guns.GunType gunType = Guns.get(gunTypeId);
        
        int added = builder.add(otherStack, gunType.maxAmmo());
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
            Guns.GunType gunType = Guns.get(gunTypeId);
            
            int added = builder.add(otherStack, gunType.maxAmmo());
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
        int totalBullets = getCompatibleBulletCount(contents);
        
        Guns.GunType gunType = Guns.get(gunTypeId);
        tooltip.add(Text.literal("Gun Type: " + gunType.displayName()));
        tooltip.add(Text.literal("Damage: " + gunType.damage()));
        tooltip.add(Text.literal("Fire Rate: " + (60.0f / gunType.fireRate()) + " shots/sec"));
        tooltip.add(Text.literal("Ammo: " + totalBullets + "/" + gunType.maxAmmo()));
        tooltip.add(Text.literal("Accepts: " + String.join(", ", gunType.acceptedAmmoTypes())));
    }
    
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true; // always show ammo bar
    }
    
    @Override
    public int getItemBarStep(ItemStack stack) {
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        int totalBullets = getCompatibleBulletCount(contents);
        Guns.GunType gunType = Guns.get(gunTypeId);
        return Math.round(13.0F * (float)totalBullets / (float)gunType.maxAmmo());
    }
    
    @Override
    public int getItemBarColor(ItemStack stack) {
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        int totalBullets = getCompatibleBulletCount(contents);
        
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
    
    private int getCompatibleBulletCount(GunContentsComponent contents) {
        // count only compatible bullets, not empty shells
        int count = 0;
        Guns.GunType gunType = Guns.get(gunTypeId);
        
        for (ItemStack itemStack : contents.items()) {
            if (itemStack.getItem() instanceof BulletItem bulletItem) {
                if (gunType.acceptsAmmo(bulletItem.getBulletTypeId())) {
                    count += itemStack.getCount();
                }
            }
        }
        
        return count;
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