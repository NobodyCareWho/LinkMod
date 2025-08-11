package org.goober.linkmod.gunstuff.items;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;

public class BulletItem extends Item {
    private final String bulletTypeId;
    public BulletItem(Settings settings, String bulletTypeId) {
        super(settings);
        this.bulletTypeId = bulletTypeId;
        
    }
    
    public String getBulletTypeId() {
        return bulletTypeId;
    }
    
    public Bullets.BulletType getBulletType() {
        return Bullets.get(bulletTypeId);
    }
    
    @Override
    public void appendTooltip(ItemStack stack,
                              Item.TooltipContext context,
                              TooltipDisplayComponent displayComponent,
                              Consumer<Text> tooltip,
                              TooltipType type) {
        // Check if it's a grenade first
        Grenades.GrenadeType grenadeType = Grenades.get(bulletTypeId);
        if (grenadeType != null) {
            // Display grenade information
            tooltip.accept(Text.literal("Type: " + grenadeType.displayName()).formatted(Formatting.GOLD));
            tooltip.accept(Text.literal("Impact Damage: " + grenadeType.impactDamageMultiplier() + "x").formatted(Formatting.RED));
            tooltip.accept(Text.literal("Explosion Size: " + grenadeType.explosionSize()).formatted(Formatting.YELLOW));
            tooltip.accept(Text.literal("Bounces: " + grenadeType.bounces()).formatted(Formatting.AQUA));
            if (grenadeType.destroysTerrain()) {
                tooltip.accept(Text.literal("Destroys Blocks").formatted(Formatting.DARK_RED));
            }
            if (grenadeType.createsFire()) {
                tooltip.accept(Text.literal("Sets Fire").formatted(Formatting.GOLD));
            }
        } else {
            // Display bullet information
            Bullets.BulletType bulletType = Bullets.get(bulletTypeId);
            if (bulletType != null) {
                tooltip.accept(Text.literal("Type: " + bulletType.displayName()).formatted(Formatting.GOLD));
                tooltip.accept(Text.literal("Damage Multiplier: " + bulletType.damageMultiplier() + "x").formatted(Formatting.RED));
                if (bulletType.pelletsPerShot() > 1) {
                    tooltip.accept(Text.literal("Pellets: " + bulletType.pelletsPerShot() + "x").formatted(Formatting.AQUA));
                }
                if (bulletType.specialBehaviour() != "none") {
                    tooltip.accept(Text.literal(bulletType.specialBehaviour()).formatted(Formatting.LIGHT_PURPLE));
                }
            }
        }
    }
}