package mekanism.client.render.item.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import mekanism.client.model.ModelIndustrialAlarm;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RenderIndustrialAlarmItem extends ItemStackTileEntityRenderer {

    private static final ModelIndustrialAlarm industrialAlarm = new ModelIndustrialAlarm();

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
        matrix.push();
        matrix.translate(0.5, 0.3, 0.5);
        industrialAlarm.render(matrix, renderer, light, overlayLight, false, 0, true, stack.hasEffect());
        matrix.pop();
    }
}