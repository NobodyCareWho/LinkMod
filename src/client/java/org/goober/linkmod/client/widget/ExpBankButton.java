package org.goober.linkmod.client.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExpBankButton extends ButtonWidget {
    private final Identifier normalTexture;
    private final Identifier hoveredTexture;
    private final Identifier pressedTexture;
    private boolean isPressed = false;
    
    public ExpBankButton(int x, int y, int width, int height, String textureName, PressAction onPress) {
        super(x, y, width, height, Text.empty(), onPress, (button) -> Text.empty());
        this.normalTexture = Identifier.of("lmod", "container/exp_bank/" + textureName);
        this.hoveredTexture = Identifier.of("lmod", "container/exp_bank/" + textureName + "selected");
        this.pressedTexture = Identifier.of("lmod", "container/exp_bank/" + textureName + "pushed");
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier texture;
        
        if (!this.active) {
            texture = normalTexture;
        } else if (this.isPressed) {
            texture = pressedTexture;
        } else if (this.isHovered()) {
            texture = hoveredTexture;
        } else {
            texture = normalTexture;
        }
        
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), this.width, this.height);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && button == 0 && this.isWithinBounds(mouseX, mouseY)) {
            this.isPressed = true;
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isPressed) {
            this.isPressed = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    private boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && 
               mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }
}