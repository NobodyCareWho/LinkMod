package org.goober.linkmod.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.goober.linkmod.screenstuff.ExpChestScreenHandler;
import org.goober.linkmod.util.ExperienceHelper;

public class ExpChestScreen extends HandledScreen<ExpChestScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("lmod", "textures/gui/container/exp_bank.png");

    private final PlayerInventory playerInventory;
    String PLAYEREXP = "0";
    String BANKEXP = "50";

    public ExpChestScreen(ExpChestScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
        this.backgroundWidth = 175;
        this.backgroundHeight = 165;
        this.playerInventoryTitleY = 72; // Position for player inventory label
        
        // Update player exp on initialization
        updatePlayerExp();
    }
    
    private void updatePlayerExp() {
        PLAYEREXP = ExperienceHelper.getPlayerTotalExp(playerInventory.player);
    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Draw the exp_bank.png background
        ctx.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);

    }

    @Override
    protected void drawForeground(DrawContext ctx, int mouseX, int mouseY) {
        // left-align text instead of centering
        ctx.drawTextWithShadow(textRenderer,
                Text.literal(PLAYEREXP),
                12, 15, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer,
                Text.literal(BANKEXP),
                91, 15, 0xFFFFFFFF);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update player exp before rendering
        updatePlayerExp();
        
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);   // no extra flush needed

    }
}