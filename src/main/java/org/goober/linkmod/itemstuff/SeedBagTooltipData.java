package org.goober.linkmod.itemstuff;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.List;

public record SeedBagTooltipData(List<ItemStack> items) implements TooltipData {
}