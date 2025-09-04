package org.goober.linkmod.entitystuff;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.goober.linkmod.gunstuff.items.GunItem;
import org.goober.linkmod.itemstuff.LmodItemRegistry;

import java.util.EnumSet;

public class GunAttackGoal <T extends HostileEntity & RangedAttackMob & CrossbowUser> extends Goal {
    public static final UniformIntProvider COOLDOWN_RANGE = TimeHelper.betweenSeconds(1, 2);
    private final T actor;
    private GunAttackGoal.Stage stage;
    private final double speed;
    private final float squaredRange;
    private int seeingTargetTicker;
    private int chargedTicksLeft;
    private int cooldown;

    public GunAttackGoal(T actor, double speed, float range) {
        this.stage = GunAttackGoal.Stage.UNCHARGED;
        this.actor = actor;
        this.speed = speed;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        return this.hasAliveTarget() && this.isEntityHoldingGun();
    }

    private boolean isEntityHoldingGun() {
        return this.actor.isHolding(stack -> stack.getItem() instanceof GunItem);
    }

    public boolean shouldContinue() {
        return this.hasAliveTarget() && (this.canStart() || !this.actor.getNavigation().isIdle()) && this.isEntityHoldingGun();
    }

    private boolean hasAliveTarget() {
        return this.actor.getTarget() != null && this.actor.getTarget().isAlive();
    }

    public void stop() {
        super.stop();
        this.actor.setAttacking(false);
        this.actor.setTarget((LivingEntity)null);
        this.seeingTargetTicker = 0;
        if (this.actor.isUsingItem()) {
            this.actor.clearActiveItem();
            ((CrossbowUser)this.actor).setCharging(false);
            this.actor.getActiveItem().set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
        }

    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity != null) {
            boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
            boolean bl2 = this.seeingTargetTicker > 0;
            if (bl != bl2) {
                this.seeingTargetTicker = 0;
            }

            if (bl) {
                ++this.seeingTargetTicker;
            } else {
                --this.seeingTargetTicker;
            }

            double d = this.actor.squaredDistanceTo(livingEntity);
            boolean bl3 = (d > (double)this.squaredRange || this.seeingTargetTicker < 5) && this.chargedTicksLeft == 0;
            if (bl3) {
                --this.cooldown;
                if (this.cooldown <= 0) {
                    this.actor.getNavigation().startMovingTo(livingEntity, this.isUncharged() ? this.speed : this.speed * 0.5);
                    this.cooldown = COOLDOWN_RANGE.get(this.actor.getRandom());
                }
            } else {
                this.cooldown = 0;
                this.actor.getNavigation().stop();
            }

            this.actor.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
            if (this.stage == GunAttackGoal.Stage.UNCHARGED) {
                if (!bl3) {
                    this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.CROSSBOW));
                    this.stage = GunAttackGoal.Stage.CHARGING;
                }
            } else if (this.stage == GunAttackGoal.Stage.CHARGING) {
                if (!this.actor.isUsingItem()) {
                    this.stage = GunAttackGoal.Stage.UNCHARGED;
                }

                int i = this.actor.getItemUseTime();
                ItemStack itemStack = this.actor.getActiveItem();
                if (i >= CrossbowItem.getPullTime(itemStack, this.actor)) {
                    this.actor.stopUsingItem();
                    this.stage = GunAttackGoal.Stage.CHARGED;
                    this.chargedTicksLeft = 20 + this.actor.getRandom().nextInt(20);
                    ((CrossbowUser)this.actor).setCharging(false);
                }
            } else if (this.stage == GunAttackGoal.Stage.CHARGED) {
                --this.chargedTicksLeft;
                if (this.chargedTicksLeft == 0) {
                    this.stage = GunAttackGoal.Stage.READY_TO_ATTACK;
                }
            } else if (this.stage == GunAttackGoal.Stage.READY_TO_ATTACK && bl) {
                ((RangedAttackMob)this.actor).shootAt(livingEntity, 1.0F);
                this.stage = GunAttackGoal.Stage.UNCHARGED;
            }

        }
    }

    private boolean isUncharged() {
        return this.stage == GunAttackGoal.Stage.UNCHARGED;
    }

    static enum Stage {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;

        private Stage() {
        }
    }
}
