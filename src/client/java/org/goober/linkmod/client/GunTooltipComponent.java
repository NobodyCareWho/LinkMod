package org.goober.linkmod.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import org.goober.linkmod.gunstuff.GunTooltipData;

public class GunTooltipComponent implements TooltipComponent {
    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 1;
    private static final int MAX_COLUMNS = 4; // Maximum columns to show
    private final GunTooltipData data;
    private final int itemCount;
    private final int columns;
    private final int rows;

    public GunTooltipComponent(GunTooltipData data) {
        this.data = data;
        // Count non-empty items
        this.itemCount = (int) data.items().stream().filter(stack -> !stack.isEmpty()).count();
        // Calculate dynamic grid size
        this.columns = Math.min(Math.max(1, itemCount), MAX_COLUMNS);
        this.rows = Math.max(1, (itemCount + columns - 1) / columns);
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return rows * SLOT_SIZE + (rows - 1) * PADDING + 4;
    }
    
    @Override
    public int getWidth(TextRenderer textRenderer) {
        return columns * SLOT_SIZE + (columns - 1) * PADDING;
    }

    @Override
    public void drawItems(TextRenderer textRenderer,
                          int x, int y,
                          int width, int height,
                          DrawContext context) {

        int itemIndex = 0;
        int slotIndex = 0;
        
        // Only draw slots for actual items
        for (int row = 0; row < rows && itemIndex < itemCount; row++) {
            for (int col = 0; col < columns && itemIndex < itemCount; col++) {
                int slotX = x + col * (SLOT_SIZE + PADDING);
                int slotY = y + row * (SLOT_SIZE + PADDING) + 1;

                // Draw slot background
                context.fillGradient(slotX, slotY,
                        slotX + SLOT_SIZE, slotY + SLOT_SIZE,
                        0x805818e, 0x8038761d);
                context.drawBorder(slotX, slotY,
                        SLOT_SIZE, SLOT_SIZE,
                        0xFF274e13);

                // Find next non-empty item
                ItemStack stack = ItemStack.EMPTY;
                while (slotIndex < data.items().size() && stack.isEmpty()) {
                    stack = data.items().get(slotIndex);
                    if (stack.isEmpty()) {
                        slotIndex++;
                    }
                }
                
                // Draw the item
                if (!stack.isEmpty()) {
                    context.drawItem(stack, slotX + 1, slotY + 1);
                    context.drawStackOverlay(textRenderer, stack, slotX + 1, slotY + 1);
                    slotIndex++;
                    itemIndex++;
                }
            }
        }
    }
}