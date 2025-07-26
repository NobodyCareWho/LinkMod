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
        Bullets.BulletType bulletType = Bullets.get(bulletTypeId);
        tooltip.accept(Text.literal("Type: " + bulletType.displayName()).formatted(Formatting.GOLD));
        tooltip.accept(Text.literal("Damage Multiplier: " + bulletType.damageMultiplier() + "x").formatted(Formatting.RED));
        if (bulletType.pelletsPerShot() > 1) {
            tooltip.accept(Text.literal("Pellets: " + bulletType.pelletsPerShot() + "x").formatted(Formatting.AQUA));
        }
        if (bulletType.specialBehaviour() != "none") {
            tooltip.accept(Text.literal("Special Behaviour: " + bulletType.specialBehaviour()).formatted(Formatting.LIGHT_PURPLE));
        }
    }
}