package mekanism.common.integration.lookingat;


import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public abstract class LookingAtElement {

    private final int borderColor;
    private final int textColor;

    protected LookingAtElement(int borderColor, int textColor) {
        this.borderColor = borderColor;
        this.textColor = textColor;
    }

    public void render(int x, int y) {
        int width = getWidth();
        int height = getHeight();
        AbstractGui.fill(x, y, x + width - 1, y + 1, borderColor);
        AbstractGui.fill(x, y, x + 1, y + height - 1, borderColor);
        AbstractGui.fill(x + width - 1, y, x + width, y + height - 1, borderColor);
        AbstractGui.fill(x, y + height - 1, x + width, y + height, borderColor);
        TextureAtlasSprite icon = getIcon();
        if (icon != null) {
            int scale = getScaledLevel(width - 2);
            if (scale > 0) {
                boolean colored = applyRenderColor();
                GuiUtils.drawTiledSprite(x + 1, y + 1, height - 2, scale, height - 2, icon, 16, 16, 0);
                if (colored) {
                    MekanismRenderer.resetColor();
                }
            }
        }
        renderScaledText(Minecraft.getInstance(), x + 4, y + 3, textColor, getWidth() - 8, getText());
    }

    public int getWidth() {
        return 100;
    }

    public int getHeight() {
        return 13;
    }

    public abstract int getScaledLevel(int level);

    @Nullable
    public abstract TextureAtlasSprite getIcon();

    public abstract ITextComponent getText();

    protected boolean applyRenderColor() {
        return false;
    }

    public static void renderScaledText(Minecraft mc, int x, int y, int color, int maxWidth, ITextComponent component) {
        int length = mc.fontRenderer.getStringWidth(component.getFormattedText());
        if (length <= maxWidth) {
            mc.fontRenderer.drawString(component.getFormattedText(), x, y, color);
        } else {
            float scale = (float) maxWidth / length;
            float reverse = 1 / scale;
            float yAdd = 4 - (scale * 8) / 2F;
            RenderSystem.pushMatrix();
            RenderSystem.scalef(scale, scale, scale);
            mc.fontRenderer.drawString(component.getFormattedText(), (int) (x * reverse), (int) ((y * reverse) + yAdd), color);
            RenderSystem.popMatrix();
        }
        //Make sure the color does not leak from having drawn the string
        MekanismRenderer.resetColor();
    }
}