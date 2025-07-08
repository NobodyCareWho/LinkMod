package org.goober.linkmod.gunstuff;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.List;

public record GunTooltipData(List<ItemStack> items) implements TooltipData {
}