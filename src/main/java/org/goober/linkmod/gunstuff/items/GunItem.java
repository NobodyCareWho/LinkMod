package org.goober.linkmod.gunstuff.items;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
import org.goober.linkmod.gunstuff.RecoilTracker;
import org.goober.linkmod.itemstuff.LmodDataComponentTypes;
import org.goober.linkmod.projectilestuff.BulletEntity;
import org.goober.linkmod.projectilestuff.PillGrenadeEntity;
import org.goober.linkmod.gunstuff.items.Bullets.BulletType;
import org.goober.linkmod.miscstuff.ParticleProfile;
import net.minecraft.entity.projectile.PersistentProjectileEntity;

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
                    
                    // skip if it's the gun's own shell type
                    if (gunType.ejectShellItemId() != null) {
                        String itemId = Registries.ITEM.getId(item.getItem()).getPath();
                        if (itemId.equals(gunType.ejectShellItemId())) {
                            continue; // skip shell casings
                        }
                    }
                    
                    if (item.getItem() instanceof BulletItem bulletItem) {
                        // check if this bullet type is compatible with this gun
                        if (gunType.acceptsBullet(bulletItem)) {
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

                    // bullettype?
                    BulletType bulletType = null;

                    if (bulletStack.getItem() instanceof BulletItem bulletItem) {
                        bulletType = bulletItem.getBulletType();

                        // shoot multiple bullets for shotgun
                        for (int i = 0; i < bulletType.pelletsPerShot(); i++) {
                            // use projectile factory to create the correct projectile type
                            PersistentProjectileEntity projectile = bulletType.projectileFactory().create(world, user, bulletStack);
                            
                            // set damage if the projectile supports it
                            if (projectile instanceof BulletEntity bullet) {
                                bullet.setDamage(gunType.damage());
                            } else if (projectile instanceof PillGrenadeEntity grenade) {
                                grenade.setDamage(gunType.damage());
                            }

                            float spread = bulletType.pelletsPerShot() > 1 ? 6.0F : 1.0F;
                            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, gunType.velocity(), spread);
                            world.spawnEntity(projectile);
                            System.out.println("Spawned projectile entity: " + projectile.getClass().getSimpleName());
                        }
                    }

                    if (gunType.spatialRecoil() > 0) {

                        float force = gunType.spatialRecoil();

                        // Get the direction the player is looking (unit vector)
                        Vec3d lookVec = user.getRotationVec(1.0F);

                        // Reverse the vector and scale by force
                        Vec3d launchVec = lookVec.multiply(-force*bulletType.sRMultiplier());

                        // Preserve some of the current Y velocity or override it
                        double yVelocity = launchVec.y * 1; // Optional lift
                        Vec3d launchVelocity = new Vec3d(launchVec.x, yVelocity, launchVec.z);
                        Vec3d oldVelocity = new Vec3d(user.getVelocity().toVector3f());
                        Vec3d finalVelocity = new Vec3d(launchVelocity.x + oldVelocity.x, launchVelocity.y + oldVelocity.y, launchVelocity.z + oldVelocity.z);
                        // Set the player's velocity
                        user.setVelocity(finalVelocity);
                        
                        // mark player as having recent recoil for fall damage reduction
                        RecoilTracker.markPlayerRecoil(user);

                        // Optionally mark for velocity update if this is server-side
                        user.velocityModified = true;
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

                    // play gun sound using sound profile
                    if (gunType.soundprofile() != null && gunType.soundprofile().primesound() != null) {
                        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                                gunType.soundprofile().primesound(),
                                SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
                    }
                    
                    // spawn fire particle from gun particle profile
                    if (gunType.particleprofile() != null && gunType.particleprofile().fireparticle() != null) {
                        if (world instanceof ServerWorld serverWorld) {
                            // get muzzle position (slightly in front of player)
                            Vec3d lookDirection = user.getRotationVec(1.0F);
                            Vec3d muzzlePos = user.getEyePos().add(lookDirection.multiply(0.5));
                            
                            serverWorld.spawnParticles(
                                gunType.particleprofile().fireparticle(),
                                muzzlePos.x, muzzlePos.y - 0.1, muzzlePos.z,
                                5, // particle count
                                0.1, 0.1, 0.1, // offset
                                0.1 // speed
                            );
                        }
                    }

                    // add cooldown
                    user.getItemCooldownManager().set(stack, gunType.cooldownTicks());

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
            
            // prioritize removing empty shells first
            ItemStack removed = removeWithPriority(contents);
            if (!removed.isEmpty()) {
                cursorStackReference.set(removed);
                // rebuild contents without the removed item
                GunContentsComponent.Builder newBuilder = new GunContentsComponent.Builder();
                for (ItemStack item : contents.items()) {
                    if (item != removed) {
                        newBuilder.add(item);
                    }
                }
                stack.set(LmodDataComponentTypes.GUN_CONTENTS, newBuilder.build());
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
    
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        GunContentsComponent contents = stack.getOrDefault(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY);
        int totalBullets = getCompatibleBulletCount(contents);
        
        Guns.GunType gunType = Guns.get(gunTypeId);
        tooltip.add(Text.literal("Gun Type: " + gunType.displayName()));
        tooltip.add(Text.literal("Damage: " + gunType.damage()));
        tooltip.add(Text.literal("Cooldown: " + gunType.cooldownTicks() + " ticks"));
        tooltip.add(Text.literal("Ammo: " + totalBullets + "/" + gunType.maxAmmo()));
        tooltip.add(Text.literal("Accepts: " + String.join(", ", gunType.acceptedAmmoTags())));
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
        
        // check if this is the gun's own ejected shell type
        if (gunType.ejectShellItemId() != null) {
            String itemId = Registries.ITEM.getId(bulletStack.getItem()).getPath();
            if (itemId.equals(gunType.ejectShellItemId())) {
                return false; // reject own shell type
            }
        }
        
        return gunType.acceptsBullet(bulletItem);
    }
    
    private int getCompatibleBulletCount(GunContentsComponent contents) {
        // count only compatible bullets, not empty shells
        int count = 0;
        Guns.GunType gunType = Guns.get(gunTypeId);
        
        for (ItemStack itemStack : contents.items()) {
            // skip if it's the gun's own shell type
            if (gunType.ejectShellItemId() != null) {
                String itemId = Registries.ITEM.getId(itemStack.getItem()).getPath();
                if (itemId.equals(gunType.ejectShellItemId())) {
                    continue; // skip shell casings
                }
            }
            
            if (itemStack.getItem() instanceof BulletItem bulletItem) {
                if (gunType.acceptsBullet(bulletItem)) {
                    count += itemStack.getCount();
                }
            }
        }
        
        return count;
    }
    
    private ItemStack removeWithPriority(GunContentsComponent contents) {
        // priority list for removal - empty shells first
        String[] priorityItems = {"bulletcasing", "shotgunshellempty"};
        
        // first, try to remove priority items (empty shells)
        for (String priorityItemId : priorityItems) {
            for (ItemStack item : contents.items()) {
                Item itemType = item.getItem();
                if (Registries.ITEM.getId(itemType).getPath().equals(priorityItemId)) {
                    return item;
                }
            }
        }
        
        // if no priority items found, remove the first item
        if (!contents.items().isEmpty()) {
            return contents.items().get(0);
        }
        
        return ItemStack.EMPTY;
    }
    
    public String getGunTypeId() {
        return gunTypeId;
    }
    
    private void playInsertSound(PlayerEntity player) {
        // use load sound from gun profile if available
        Guns.GunType gunType = Guns.get(gunTypeId);
        if (gunType.soundprofile() != null && gunType.soundprofile().loadsound() != null) {
            player.playSound(gunType.soundprofile().loadsound(), 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
        } else {
            player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
        }
    }
    
    private void playInsertFailSound(PlayerEntity player) {
        player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }
    
    private void playRemoveOneSound(PlayerEntity player) {
        // use unload sound from gun profile if available
        Guns.GunType gunType = Guns.get(gunTypeId);
        if (gunType.soundprofile() != null && gunType.soundprofile().unloadsound() != null) {
            player.playSound(gunType.soundprofile().unloadsound(), 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
        } else {
            player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.getWorld().getRandom().nextFloat() * 0.4F);
        }
    }
}