package org.goober.linkmod.gunstuff.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import java.util.List;

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
    
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        Bullets.BulletType bulletType = Bullets.get(bulletTypeId);
        tooltip.add(Text.literal("Type: " + bulletType.displayName()));
        tooltip.add(Text.literal("Damage Multiplier: " + bulletType.damageMultiplier() + "x"));
        if (bulletType.pelletsPerShot() > 1) {
            tooltip.add(Text.literal("Pellets: " + bulletType.pelletsPerShot() + "x"));
        }
    }
}