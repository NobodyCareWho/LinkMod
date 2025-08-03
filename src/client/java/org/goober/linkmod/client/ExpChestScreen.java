package org.goober.linkmod.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundEvents;
import org.goober.linkmod.screenstuff.ExpChestScreenHandler;
import org.goober.linkmod.util.ExperienceHelper;
import org.goober.linkmod.client.widget.ExpBankButton;

public class ExpChestScreen extends HandledScreen<ExpChestScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("lmod", "textures/gui/container/exp_bank.png");
    private static final Identifier TEXT_FIELD_TEXTURE = Identifier.of("lmod", "container/exp_bank/textfield");

    private final PlayerInventory playerInventory;
    private TextFieldWidget amountField;
    private ExpBankButton deposit1Button;
    private ExpBankButton deposit2Button;
    private ExpBankButton withdraw1Button;
    private ExpBankButton withdraw2Button;
    String PLAYEREXP = "0";
    String BANKEXP = "0";

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
        BANKEXP = ExperienceHelper.getBankedExp(playerInventory.player);
    }
    
    private void playClickSound() {
        if (this.client != null && this.client.player != null) {
            this.client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.25F, 1.0F);
        }
    }
    
    private void handleButtonClick() {
        playClickSound();
        // Refocus the text field after button click
        this.setFocused(this.amountField);
        this.amountField.setFocused(true);
    }
    
    private void playExpSound() {
        if (this.client != null && this.client.player != null) {
            this.client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);
        }
    }
    
    private void playLevelUpSound() {
        if (this.client != null && this.client.player != null) {
            this.client.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.5F, 1.0F);
        }
    }
    
    private void playBlockedSound() {
        if (this.client != null && this.client.player != null) {
            this.client.player.playSound(SoundEvents.BLOCK_DISPENSER_FAIL, 0.25F, 1.0F);
        }
    }
    
    private void refocusTextField() {
        // Focus is now handled in mouseClicked override
    }
    
    @Override
    protected void init() {
        super.init();
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        
        // Create text field for entering exp amount to deposit/withdraw
        this.amountField = new TextFieldWidget(this.textRenderer, x + 13, y + 48, 75, 12, Text.literal("Amount"));
        this.amountField.setFocusUnlocked(false);
        this.amountField.setEditableColor(-1);
        this.amountField.setUneditableColor(-1);
        this.amountField.setDrawsBackground(false);
        this.amountField.setMaxLength(10);
        this.amountField.setChangedListener(this::onAmountChanged);
        this.amountField.setText("0");
        this.addDrawableChild(this.amountField);
        this.setInitialFocus(this.amountField);

        // Calculate button positions relative to GUI
        int buttonBaseY = y + 60; 
        
        // Deposit with text field button
        this.deposit1Button = new ExpBankButton(x + 125, buttonBaseY, 14, 14, "depot1", button -> {
            String amountText = this.amountField.getText();
            if (!amountText.isEmpty()) {
                int amount = Integer.parseInt(amountText);
                if (amount > 0) {
                    boolean success = ExperienceHelper.depositExp(playerInventory.player, amount);
                    updatePlayerExp();
                    if (success) {
                        playExpSound();
                    } else {
                        playBlockedSound();
                    }
                    refocusTextField();
                }
            }
        });
        this.addDrawableChild(this.deposit1Button);

        // Deposit all button
        this.deposit2Button = new ExpBankButton(x + 125 + 26, buttonBaseY, 14, 14, "depot2", button -> {
            int totalExp = Integer.parseInt(ExperienceHelper.getPlayerTotalExp(playerInventory.player));
            if (totalExp > 0) {
                boolean success = ExperienceHelper.depositExp(playerInventory.player, totalExp);
                updatePlayerExp();
                if (success) {
                    playLevelUpSound();
                } else {
                    playBlockedSound();
                }
                // Refocus text field
                refocusTextField();
            } else {
                playBlockedSound();
            }
        });
        this.addDrawableChild(this.deposit2Button);
        
        // Withdraw with text field button
        this.withdraw1Button = new ExpBankButton(x + 125, buttonBaseY - 30, 14, 14, "withdraw1", button -> {
            String amountText = this.amountField.getText();
            if (!amountText.isEmpty()) {
                int amount = Integer.parseInt(amountText);
                if (amount > 0) {
                    boolean success = ExperienceHelper.withdrawExp(playerInventory.player, amount);
                    updatePlayerExp();
                    if (success) {
                        playExpSound();
                    } else {
                        playBlockedSound();
                    }
                    refocusTextField();
                }
            }
        });
        this.addDrawableChild(this.withdraw1Button);

        // Withdraw all button
        this.withdraw2Button = new ExpBankButton(x + 125 + 26, buttonBaseY - 30, 14, 14, "withdraw2", button -> {
            int bankedExp = Integer.parseInt(ExperienceHelper.getBankedExp(playerInventory.player));
            if (bankedExp > 0) {
                boolean success = ExperienceHelper.withdrawExp(playerInventory.player, bankedExp);
                updatePlayerExp();
                if (success) {
                    playLevelUpSound();
                } else {
                    playBlockedSound();
                }
                // Refocus text field
                refocusTextField();
            } else {
                playBlockedSound();
            }
        });
        this.addDrawableChild(this.withdraw2Button);
    }
    
    private void onAmountChanged(String text) {
        // Only allow numeric input
        if (!text.isEmpty() && !text.matches("\\d+")) {
            // Remove non-numeric characters
            String filtered = text.replaceAll("[^\\d]", "");
            this.amountField.setText(filtered);
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC key
            this.close();
            return true;
        }
        
        return this.amountField.keyPressed(keyCode, scanCode, modifiers) || 
               this.amountField.isActive() || 
               super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if clicking on a button
        boolean isButtonClick = false;
        if (this.deposit1Button.isMouseOver(mouseX, mouseY) ||
            this.deposit2Button.isMouseOver(mouseX, mouseY) ||
            this.withdraw1Button.isMouseOver(mouseX, mouseY) ||
            this.withdraw2Button.isMouseOver(mouseX, mouseY)) {
            isButtonClick = true;
        }
        
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        
        // If a button was clicked, keep text field focused
        if (isButtonClick && result) {
            // Don't change focus
            return result;
        }
        
        return result;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean result = super.mouseReleased(mouseX, mouseY, button);
        
        // After releasing mouse on a button, refocus text field
        if (button == 0) {
            this.setFocused(this.amountField);
            this.amountField.setFocused(true);
        }
        
        return result;
    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Draw the exp_bank.png background
        ctx.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
        
        // Draw the text field background
        ctx.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXT_FIELD_TEXTURE, x + 11, y + 46, 75, 12);
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
        
        // Check if mouse is over the tooltip areas and render tooltips
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        int tooltipX = x + 89;
        int tooltipY = y + 26;
        
        // tooltips area
        if (mouseX >= tooltipX && mouseX < tooltipX + 10 && 
            mouseY >= tooltipY && mouseY < tooltipY + 10) {
            // Render tooltip
            context.drawTooltip(this.textRenderer, Text.literal("Bank Experience"), mouseX, mouseY);
        }

        int tooltip2X = tooltipX - 79;
        if (mouseX >= tooltip2X && mouseX < tooltip2X + 10 && 
            mouseY >= tooltipY && mouseY < tooltipY + 10) {
            // Render tooltip
            context.drawTooltip(this.textRenderer, Text.literal("Player Experience"), mouseX, mouseY);
        }
    }
}