package org.goober.linkmod.entitystuff;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.*;
import org.goober.linkmod.itemstuff.LmodItemRegistry;
import org.jetbrains.annotations.Nullable;

public class AgentPillagerEntity extends IllagerEntity implements CrossbowUser, InventoryOwner {
    private static final TrackedData<Boolean> CHARGING;
    private static final int field_30478 = 5;
    private static final int field_30476 = 300;
    private final SimpleInventory inventory = new SimpleInventory(5);

    public AgentPillagerEntity(EntityType<? extends AgentPillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal(this, CreakingEntity.class, 8.0F, 1.0, 1.2));
        this.goalSelector.add(2, new RaiderEntity.PatrolApproachGoal(this, 10.0F));
        this.goalSelector.add(3, new CrossbowAttackGoal(this, 1.0, 8.0F));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0F));
        this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createPillagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.3499999940395355).add(EntityAttributes.MAX_HEALTH, 24.0).add(EntityAttributes.ATTACK_DAMAGE, 5.0).add(EntityAttributes.FOLLOW_RANGE, 32.0);
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHARGING, false);
    }

    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return weapon == Items.CROSSBOW;
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
        return ItemTags.PILLAGER_PREFERRED_WEAPONS;
    }

    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        this.writeInventory(view);
    }

    public IllagerEntity.State getState() {
        if (this.isCharging()) {
            return State.CROSSBOW_CHARGE;
        } else if (this.isHolding(Items.CROSSBOW)) {
            return State.CROSSBOW_HOLD;
        } else {
            return this.isAttacking() ? State.ATTACKING : State.NEUTRAL;
        }
    }

    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.readInventory(view);
        this.setCanPickUpLoot(true);
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0F;
    }

    public int getLimitPerChunk() {
        return 1;
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(LmodItemRegistry.EJECTORPISTOL));
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

    static {
        CHARGING = DataTracker.registerData(AgentPillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
