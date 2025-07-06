package org.goober.linkmod.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import org.goober.linkmod.itemstuff.SeedBagTooltipData;

public class SeedBagTooltipComponent implements TooltipComponent {
    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 1;
    private final SeedBagTooltipData data;
    
    public SeedBagTooltipComponent(SeedBagTooltipData data) {
        this.data = data;
    }
    
    @Override
    public int getHeight(TextRenderer textRenderer) {
        return data.getRows() * SLOT_SIZE + (data.getRows() - 1) * PADDING + 4;
    }
    
    @Override
    public int getWidth(TextRenderer textRenderer) {
        return data.getColumns() * SLOT_SIZE + (data.getColumns() - 1) * PADDING;
    }

    @Override
    public void drawItems(TextRenderer textRenderer,
                          int x, int y,
                          int width, int height,          // ➊ new parameters in 1.21.6
                          DrawContext context) {

        int index = 0;
        for (int row = 0; row < data.getRows(); row++) {
            for (int col = 0; col < data.getColumns(); col++) {

                int slotX = x + col * (SLOT_SIZE + PADDING);
                int slotY = y + row * (SLOT_SIZE + PADDING) + 1;

                /* slot background */
                context.fillGradient(slotX, slotY,
                        slotX + SLOT_SIZE, slotY + SLOT_SIZE,
                        0x80000000, 0x80000000);
                context.drawBorder(slotX, slotY,
                        SLOT_SIZE, SLOT_SIZE,
                        0xFF8B8B8B);

                /* item stack */
                if (index < data.items().size()) {
                    ItemStack stack = data.items().get(index);
                    if (!stack.isEmpty()) {
                        // ➋ draw the sprite
                        context.drawItem(stack, slotX + 1, slotY + 1);
                        // ➌ draw overlay (count, durability bar, etc.)
                        context.drawStackOverlay(textRenderer, stack, slotX + 1, slotY + 1);
                    }
                }
                index++;
            }
        }
    }
}