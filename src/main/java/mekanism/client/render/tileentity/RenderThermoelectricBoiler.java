package mekanism.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.ModelRenderer;
import mekanism.client.render.data.FluidRenderData;
import mekanism.client.render.data.GasRenderData;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RenderThermoelectricBoiler extends MekanismTileEntityRenderer<TileEntityBoilerCasing> {

    public RenderThermoelectricBoiler(TileEntityRendererDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityBoilerCasing tile, float partialTick, MatrixStack matrix, IRenderTypeBuffer renderer, int light, int overlayLight, IProfiler profiler) {
        if (tile.isMaster) {
            BoilerMultiblockData multiblock = tile.getMultiblock();
            if (multiblock.isFormed() && multiblock.renderLocation != null && multiblock.upperRenderLocation != null) {
                BlockPos pos = tile.getPos();
                IVertexBuilder buffer = null;
                if (!multiblock.waterTank.isEmpty()) {
                    int height = multiblock.upperRenderLocation.getY() - 1 - multiblock.renderLocation.getY();
                    if (height >= 1) {
                        FluidRenderData data = new FluidRenderData(multiblock.waterTank.getFluid());
                        data.location = multiblock.renderLocation;
                        data.height = height;
                        data.length = multiblock.length();
                        data.width = multiblock.width();
                        int glow = data.calculateGlowLight(LightTexture.packLight(0, 15));
                        matrix.push();
                        matrix.translate(data.location.getX() - pos.getX(), data.location.getY() - pos.getY(), data.location.getZ() - pos.getZ());
                        buffer = renderer.getBuffer(Atlases.getTranslucentCullBlockType());
                        MekanismRenderer.renderObject(ModelRenderer.getModel(data, multiblock.prevWaterScale), matrix, buffer,
                              data.getColorARGB(multiblock.prevWaterScale), glow, overlayLight);
                        matrix.pop();

                        MekanismRenderer.renderValves(matrix, buffer, multiblock.valves, data, pos, glow, overlayLight);
                    }
                }
                if (!multiblock.steamTank.isEmpty()) {
                    int height = multiblock.renderLocation.getY() + multiblock.height() - 2 - multiblock.upperRenderLocation.getY();
                    if (height >= 1) {
                        GasRenderData data = new GasRenderData(multiblock.steamTank.getStack());
                        data.location = multiblock.upperRenderLocation;
                        data.height = height;
                        data.length = multiblock.length();
                        data.width = multiblock.width();
                        if (buffer == null) {
                            buffer = renderer.getBuffer(Atlases.getTranslucentCullBlockType());
                        }
                        int glow = data.calculateGlowLight(LightTexture.packLight(0, 15));
                        matrix.push();
                        matrix.translate(data.location.getX() - pos.getX(), data.location.getY() - pos.getY(), data.location.getZ() - pos.getZ());
                        Model3D gasModel = ModelRenderer.getModel(data, 1);
                        MekanismRenderer.renderObject(gasModel, matrix, buffer, data.getColorARGB(multiblock.prevSteamScale), glow, overlayLight);
                        matrix.pop();
                    }
                }
            }
        }
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.THERMOELECTRIC_BOILER;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityBoilerCasing tile) {
        if (tile.isMaster) {
            BoilerMultiblockData multiblock = tile.getMultiblock();
            return multiblock.isFormed() && multiblock.renderLocation != null && multiblock.upperRenderLocation != null;
        }
        return false;
    }
}