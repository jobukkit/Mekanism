package mekanism.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.data.FluidRenderData;
import mekanism.client.render.data.RenderData;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Lazy;

@NothingNullByDefault
public class RenderThermoelectricBoiler extends MultiblockTileEntityRenderer<BoilerMultiblockData, TileEntityBoilerCasing> {

    public RenderThermoelectricBoiler(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void render(TileEntityBoilerCasing tile, BoilerMultiblockData multiblock, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light,
          int overlayLight, ProfilerFiller profiler) {
        BlockPos pos = tile.getBlockPos();
        Lazy<VertexConsumer> buffer = Lazy.of(() -> renderer.getBuffer(Sheets.translucentCullBlockSheet()));
        if (!multiblock.waterTank.isEmpty()) {
            int height = multiblock.upperRenderLocation.getY() - 1 - multiblock.renderLocation.getY();
            if (height > 0) {
                FluidRenderData data = RenderData.Builder.create(multiblock.waterTank.getFluid())
                      .of(multiblock)
                      .height(height)
                      .build();
                renderObject(data, multiblock.valves, pos, matrix, buffer.get(), overlayLight, multiblock.prevWaterScale);
            }
        }
        if (!multiblock.steamTank.isEmpty()) {
            int height = multiblock.renderLocation.getY() + multiblock.height() - 2 - multiblock.upperRenderLocation.getY();
            if (height > 0) {
                RenderData data = RenderData.Builder.create(multiblock.steamTank.getStack())
                      .of(multiblock)
                      .location(multiblock.upperRenderLocation)
                      .height(height)
                      .build();
                renderObject(data, pos, matrix, buffer.get(), overlayLight, multiblock.prevSteamScale);
            }
        }
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.THERMOELECTRIC_BOILER;
    }

    @Override
    protected boolean shouldRender(TileEntityBoilerCasing tile, BoilerMultiblockData multiblock, Vec3 camera) {
        return super.shouldRender(tile, multiblock, camera) && multiblock.upperRenderLocation != null;
    }
}