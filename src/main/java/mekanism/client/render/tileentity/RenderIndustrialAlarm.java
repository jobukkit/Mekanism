package mekanism.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.model.ModelIndustrialAlarm;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.render.RenderTickHandler.LazyRender;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.tile.TileEntityIndustrialAlarm;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;

@NothingNullByDefault
public class RenderIndustrialAlarm extends ModelTileEntityRenderer<TileEntityIndustrialAlarm, ModelIndustrialAlarm> {

    private static final float ROTATE_SPEED = 10F;

    public RenderIndustrialAlarm(BlockEntityRendererProvider.Context context) {
        super(context, ModelIndustrialAlarm::new);
    }

    @Override
    protected void render(TileEntityIndustrialAlarm tile, float partialTicks, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight,
          ProfilerFiller profiler) {
        RenderTickHandler.addTransparentRenderer(model.getRenderType(), new LazyRender() {
            @Override
            public void render(Camera camera, VertexConsumer buffer, PoseStack poseStack, int renderTick, float partialTick, ProfilerFiller profiler) {
                float rot = (renderTick + partialTick) * ROTATE_SPEED % 360;
                Vec3 renderPos = Vec3.atBottomCenterOf(tile.getBlockPos());
                poseStack.pushPose();
                poseStack.translate(renderPos.x, renderPos.y, renderPos.z);
                switch (tile.getDirection()) {
                    case DOWN -> {
                        poseStack.translate(0, 1, 0);
                        poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                    }
                    case NORTH -> {
                        poseStack.translate(0, 0.5, 0.5);
                        poseStack.mulPose(Vector3f.XN.rotationDegrees(90));
                    }
                    case SOUTH -> {
                        poseStack.translate(0, 0.5, -0.5);
                        poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
                    }
                    case EAST -> {
                        poseStack.translate(-0.5, 0.5, 0);
                        poseStack.mulPose(Vector3f.ZN.rotationDegrees(90));
                    }
                    case WEST -> {
                        poseStack.translate(0.5, 0.5, 0);
                        poseStack.mulPose(Vector3f.ZP.rotationDegrees(90));
                    }
                }
                model.render(poseStack, buffer, LightTexture.FULL_BRIGHT, overlayLight, 1, 1, 1, 1, rot);
                poseStack.popPose();
            }

            @Override
            public Vec3 getCenterPos(float partialTick) {
                //Centered position, does not need to be cached as it is only called once
                return Vec3.atCenterOf(tile.getBlockPos());
            }

            @Override
            public String getProfilerSection() {
                return ProfilerConstants.INDUSTRIAL_ALARM;
            }
        });
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.INDUSTRIAL_ALARM;
    }

    @Override
    public boolean shouldRender(TileEntityIndustrialAlarm tile, Vec3 camera) {
        return tile.getActive() && super.shouldRender(tile, camera);
    }
}