package mekanism.client.render.item.gear;

import com.mojang.blaze3d.matrix.MatrixStack;
import mekanism.client.model.ModelScubaMask;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RenderScubaMask extends ItemStackTileEntityRenderer {

    private static final ModelScubaMask scubaMask = new ModelScubaMask();

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.rotate(Vector3f.ZP.rotationDegrees(180));
        scubaMask.render(matrix, renderer, light, overlayLight, stack.hasEffect());
        matrix.pop();
    }
}