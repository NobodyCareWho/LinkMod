package org.goober.linkmod.entitystuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.goober.linkmod.gunstuff.items.Bullets;
import org.goober.linkmod.gunstuff.items.GunItem;
import org.goober.linkmod.gunstuff.items.Guns;
import org.goober.linkmod.itemstuff.LmodDataComponentTypes;
import org.goober.linkmod.itemstuff.LmodItemRegistry;
import org.goober.linkmod.projectilestuff.BulletEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;

public class DesperadoEntity extends IllagerEntity implements CrossbowUser, InventoryOwner {
    private static final TrackedData<Boolean> CHARGING;
    private static final TrackedData<Integer> VARIANT;
    private static final int field_30478 = 5;
    private static final int field_30476 = 300;
    private final SimpleInventory inventory = new SimpleInventory(5);
    private int rifleAttackCooldown = 0;
    private int currentAmmo = -1; // -1 means not initialized
    private boolean isReloading = false;

    public DesperadoEntity(EntityType<? extends DesperadoEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal(this, CreakingEntity.class, 8.0F, 1.0, 1.2));
        this.goalSelector.add(2, new PatrolApproachGoal(this, 10.0F));
        this.goalSelector.add(3, new GunAttackGoal(this, 1.0, 8.0F));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 30.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 30.0F));
        this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createPillagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.3499999940395355).add(EntityAttributes.MAX_HEALTH, 24.0).add(EntityAttributes.ATTACK_DAMAGE, 5.0).add(EntityAttributes.FOLLOW_RANGE, 32.0);
    }
    
    public static DefaultAttributeContainer.Builder createHostileAttributes() {
        return createPillagerAttributes();
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHARGING, false);
        builder.add(VARIANT, DesperadoEntity.Variant.DEFAULT.index);
    }

    private void canUseRangedWeapon(CallbackInfoReturnable<Boolean> cir) {
        DesperadoEntity agentPillager = (DesperadoEntity)(Object)this;
        ItemStack mainHand = agentPillager.getStackInHand(Hand.MAIN_HAND);
        if (mainHand.getItem() instanceof GunItem) {
            // Make pillager AI think it's holding a crossbow
            cir.setReturnValue(true);
        }
    }



    public boolean isCharging() {
        return (Boolean)this.dataTracker.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.dataTracker.set(CHARGING, charging);
    }

    public void postShoot() {
        this.despawnCounter = 0;
    }

    public TagKey<Item> getPreferredWeapons() {
        return null;
    }

    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put("Desperado", DesperadoEntity.Variant.INDEX_CODEC, this.getVariant());
        this.writeInventory(view);
    }

    public State getState() {
        if (this.isCharging()) {
            return State.CROSSBOW_CHARGE;
        } else if (isHoldingGun()) {
            return State.CROSSBOW_HOLD;
        } else {
            return this.isAttacking() ? State.ATTACKING : State.NEUTRAL;
        }
    }
    private boolean isHoldingGun() {
        return this.getMainHandStack().getItem() instanceof GunItem
                || this.getOffHandStack().getItem() instanceof GunItem;
    }

    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.readInventory(view);
        this.setVariant((DesperadoEntity.Variant)view.read("DesperadoType", DesperadoEntity.Variant.INDEX_CODEC).orElse(DesperadoEntity.Variant.DEFAULT));
        this.setCanPickUpLoot(true);
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0F;
    }

    public int getLimitPerChunk() {
        return 1;
    }

    public DesperadoEntity.Variant getVariant() {
        return DesperadoEntity.Variant.byIndex((Integer)this.dataTracker.get(VARIANT));
    }

    private void setVariant(DesperadoEntity.Variant variant) {
        this.dataTracker.set(VARIANT, variant.index);
    }

    public <T> T get(ComponentType<? extends T> type) {
        return type == LmodDataComponentTypes.DESPERADO_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
    }
    protected void copyComponentsFrom(ComponentsAccess from) {
        this.copyComponentFrom(from, LmodDataComponentTypes.DESPERADO_VARIANT);
        super.copyComponentsFrom(from);
    }
    protected <T> boolean setApplicableComponent(ComponentType<T> type, T value) {
        if (type == LmodDataComponentTypes.DESPERADO_VARIANT) {
            this.setVariant((DesperadoEntity.Variant)castComponentValue(LmodDataComponentTypes.DESPERADO_VARIANT, value));
            return true;
        } else {
            return super.setApplicableComponent(type, value);
        }
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
        DesperadoEntity.Variant variant = getVariantFromPos(world, this.getBlockPos());
        if (entityData instanceof DesperadoEntity.DesperadoData) {
            variant = ((DesperadoEntity.DesperadoData)entityData).variant;
        } else {
            entityData = new DesperadoEntity.DesperadoData(variant);
        }

        this.setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    private static DesperadoEntity.Variant getVariantFromPos(WorldAccess world, BlockPos pos) {
        RegistryEntry<Biome> registryEntry = world.getBiome(pos);
        int i = world.getRandom().nextInt(100);
        if (registryEntry.isIn(BiomeTags.IS_BADLANDS)) {
            return DesperadoEntity.Variant.RED;
        } else {
        return DesperadoEntity.Variant.BLUE; }
    }

    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        if (this.random.nextFloat() < 0.75f) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(LmodItemRegistry.EJECTORPISTOL));
        } else {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(LmodItemRegistry.PUMPSG));
        }
    }


    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    public void shootAt(LivingEntity target, float pullProgress) {
        this.shoot(this, 1.6F);
    }

    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        if (!this.getWorld().isClient && this.isAlive()) {
            if (rifleAttackCooldown > 0) {
                rifleAttackCooldown--;
                if (isReloading && rifleAttackCooldown == 0) {
                    // Reload complete
                    ((CrossbowUser)this).setCharging(false);
                    isReloading = false;
                    ItemStack mainHand = this.getStackInHand(Hand.MAIN_HAND);
                    if (mainHand.getItem() instanceof GunItem gunItem) {
                        Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                        currentAmmo = gunType.maxAmmo();
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                                gunType.soundprofile().loadsound(),
                                SoundCategory.HOSTILE, 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));

                    }
                }
            }

            ItemStack mainHand = this.getStackInHand(Hand.MAIN_HAND);
            if (mainHand.getItem() instanceof GunItem gunItem && rifleAttackCooldown == 0 && !isReloading) {
                // Initialize ammo if needed
                if (currentAmmo == -1) {
                    Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                    currentAmmo = gunType.maxAmmo();
                }

                LivingEntity target = this.getTarget();
                if (target != null && this.canSee(target) && currentAmmo > 0) {
                    double distance = this.squaredDistanceTo(target);
                    Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                    // Attack if within 20 blocks
                    if (distance < 400.0 && gunType.acceptedAmmoTags().equals(Set.of("rifle_ammo")) || distance < 150.0) {
                        this.lookAtEntity(target, 30.0F, 30.0F);
                        this.shootAtTarget(target);
                        currentAmmo--;

                        // Check if we need to reload
                        if (currentAmmo <= 0) {
                            ((CrossbowUser)this).setCharging(true);
                            isReloading = true;
                            rifleAttackCooldown = 65; // 3.25 second reload for pillager
                            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                                    gunType.soundprofile().unloadsound(),
                                    SoundCategory.HOSTILE, 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));
                        } else {
                            // Normal attack cooldown

                            rifleAttackCooldown = gunType.cooldownTicks() + random.nextBetween(0,15);
                        }
                    }
                }
            }
        }
    }
    
    private void shootAtTarget(LivingEntity target) {
            ItemStack rifle = this.getStackInHand(Hand.MAIN_HAND);
            if (rifle.getItem() instanceof GunItem gunItem && this.getWorld() instanceof ServerWorld) {
                // Get gun type info
                Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                Bullets.BulletType bulletType = null;
                ItemStack bulletStack = ItemStack.EMPTY;

                if (gunType.acceptedAmmoTags().equals(Set.of("rifle_ammo"))) {
                    // rifle ammo
                    bulletType = Bullets.get("copper_bullet");
                    bulletStack = new ItemStack(LmodItemRegistry.COPPER_BULLET);

                } else if (gunType.acceptedAmmoTags().equals(Set.of("shotgun_shells"))) {
                    // buckshot
                    bulletType = Bullets.get("buckshot");
                    bulletStack = new ItemStack(LmodItemRegistry.BUCKSHELL);

                } else if (gunType.acceptedAmmoTags().equals(Set.of("grenade_shells"))) {
                    // pill grenades
                    bulletType = Bullets.get("thumpershell");
                    bulletStack = new ItemStack(LmodItemRegistry.THUMPERSHELL);
                }
                // Create a copper bullet stack for projectile creation

                float maxSpread = gunType.baseInaccuracy() + bulletType.baseSpreadIncrease();

                for (int i = 0; i < bulletType.pelletsPerShot(); i++) {
                    // Create bullet projectile directly (like skeleton arrows)
                    BulletEntity projectile = new BulletEntity(this.getWorld(), this, bulletStack);

                    // Set reduced damage for gun shots
                    projectile.setDamage(gunType.damage() * 0.7);

                    // Calculate velocity and spread
                    Vec3d targetPos = target.getEyePos();
                    Vec3d shooterPos = this.getEyePos();
                    Vec3d direction = targetPos.subtract(shooterPos).normalize();

                    // Add some inaccuracy
                    float spread = this.random.nextFloat() * maxSpread;
                    direction = direction.add(
                            (this.random.nextFloat() - 0.5) * spread * 0.1,
                            (this.random.nextFloat() - 0.5) * spread * 0.1,
                            (this.random.nextFloat() - 0.5) * spread * 0.1
                    ).normalize();

                    // Set projectile velocity
                    projectile.setVelocity(direction.x, direction.y, direction.z, gunType.velocity() * bulletType.vMultiplier(), spread);

                    // Spawn the projectile
                    this.getWorld().spawnEntity(projectile);

                }

                if (bulletType.particleprofile() != null && bulletType.particleprofile().fireparticle() != null) {
                    if (this.getWorld() instanceof ServerWorld serverWorld) {
                        // get muzzle position (slightly in front of player)
                        Vec3d lookDirection = this.getRotationVec(1.0F);
                        Vec3d muzzlePos = this.getEyePos().add(lookDirection.multiply(0.5));

                        serverWorld.spawnParticles(
                                bulletType.particleprofile().fireparticle(),
                                muzzlePos.x, muzzlePos.y - 0.1, muzzlePos.z,
                                1, // particle count
                                0.1, -0.3, 0.1, // offset
                                0.1 // speed
                        );
                    }
                }
                // Play gun sound
                if (bulletType.soundprofile() != null && bulletType.soundprofile().firesound() != null) {
                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                            bulletType.soundprofile().firesound(),
                            SoundCategory.HOSTILE, 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));
                }

                // Play attack animation
                this.swingHand(Hand.MAIN_HAND);
            }
    }



    public SimpleInventory getInventory() {
        return this.inventory;
    }

    protected void loot(ServerWorld world, ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getStack();
        if (itemStack.getItem() instanceof BannerItem) {
            super.loot(world, itemEntity);
        } else if (this.isRaidCaptain(itemStack)) {
            this.triggerItemPickedUpByEntityCriteria(itemEntity);
            ItemStack itemStack2 = this.inventory.addStack(itemStack);
            if (itemStack2.isEmpty()) {
                itemEntity.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }

    }

    private boolean isRaidCaptain(ItemStack stack) {
        return this.hasActiveRaid() && stack.isOf(Items.WHITE_BANNER);
    }

    public StackReference getStackReference(int mappedIndex) {
        int i = mappedIndex - 300;
        return i >= 0 && i < this.inventory.size() ? StackReference.of(this.inventory, i) : super.getStackReference(mappedIndex);
    }

    public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
        Raid raid = this.getRaid();



    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }

    public static enum Variant implements StringIdentifiable {
        RED(0, "red"),
        BLUE(1, "blue");

        public static final DesperadoEntity.Variant DEFAULT = BLUE;
        private static final IntFunction<DesperadoEntity.Variant> INDEX_MAPPER = ValueLists.createIndexToValueFunction(DesperadoEntity.Variant::getIndex, values(), DEFAULT);
        public static final Codec<DesperadoEntity.Variant> CODEC = StringIdentifiable.createCodec(DesperadoEntity.Variant::values);
        /** @deprecated */
        @Deprecated
        public static final Codec<DesperadoEntity.Variant> INDEX_CODEC;
        public static final PacketCodec<ByteBuf, DesperadoEntity.Variant> PACKET_CODEC;
        final int index;
        private final String id;

        private Variant(final int index, final String id) {
            this.index = index;
            this.id = id;
        }

        public String asString() {
            return this.id;
        }

        public int getIndex() {
            return this.index;
        }

        public static DesperadoEntity.Variant byIndex(int index) {
            return (DesperadoEntity.Variant)INDEX_MAPPER.apply(index);
        }

        static {
            INDEX_CODEC = Codec.INT.xmap(INDEX_MAPPER::apply, DesperadoEntity.Variant::getIndex);
            PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, DesperadoEntity.Variant::getIndex);
        }
    }

    static {
        VARIANT = DataTracker.registerData(DesperadoEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CHARGING = DataTracker.registerData(DesperadoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    public static class DesperadoData extends PassiveEntity.PassiveData {
        public final DesperadoEntity.Variant variant;

        public DesperadoData(DesperadoEntity.Variant variant) {
            super(1.0F);
            this.variant = variant;
        }
    }
}
