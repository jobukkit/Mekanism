package mekanism.generators.client.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.generators.client.model.ModelWindGenerator;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class RenderWindGeneratorItem extends ItemStackTileEntityRenderer {

    private static final ModelWindGenerator windGenerator = new ModelWindGenerator();
    private static float lastTicksUpdated = 0;
    private static int angle = 0;

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull TransformType transformType, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
        float renderPartialTicks = Minecraft.getInstance().getFrameTime();
        if (lastTicksUpdated != renderPartialTicks) {
            //Only update the angle if we are in a world and that world is not blacklisted
            if (Minecraft.getInstance().level != null) {
                List<ResourceLocation> blacklistedDimensions = MekanismGeneratorsConfig.generators.windGenerationDimBlacklist.get();
                if (blacklistedDimensions.isEmpty() || !blacklistedDimensions.contains(Minecraft.getInstance().level.dimension().location())) {
                    angle = (angle + 2) % 360;
                }
            }
            lastTicksUpdated = renderPartialTicks;
        }
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(Vector3f.ZP.rotationDegrees(180));
        windGenerator.render(matrix, renderer, angle, light, overlayLight, stack.hasFoil());
        matrix.popPose();
    }
}