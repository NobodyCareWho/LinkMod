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
        } else if (this.isSelected()) {
            texture = pressedTexture;
        } else if (this.isHovered()) {
            texture = hoveredTexture;
        } else {
            texture = normalTexture;
        }
        
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), this.width, this.height);
    }
}