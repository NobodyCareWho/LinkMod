package org.goober.linkmod.itemstuff;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.List;

public record SeedBagTooltipData(List<ItemStack> items) implements TooltipData {
    public int getSlotCount() {
        return 8;
    }
    
    public int getColumns() {
        return 4;
    }
    
    public int getRows() {
        return 2;
    }
}