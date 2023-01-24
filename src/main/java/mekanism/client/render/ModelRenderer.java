package mekanism.client.render;

import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.data.RenderData;
import mekanism.client.render.data.ValveRenderData;
import mekanism.common.util.MekanismUtils;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public final class ModelRenderer {

    private ModelRenderer() {
    }

    private static final int BLOCK_STAGES = 1_000;

    private static final Map<RenderData, Int2ObjectMap<Model3D>> cachedCenterData = new Object2ObjectOpenHashMap<>();
    private static final Map<ValveRenderData, Float2ObjectMap<Model3D>> cachedValveFluids = new Object2ObjectOpenHashMap<>();

    public static int getStage(FluidStack stack, int stages, double scale) {
        return getStage(MekanismUtils.lighterThanAirGas(stack), stages, scale);
    }

    public static int getStage(boolean gaseous, int stages, double scale) {
        if (gaseous) {
            return stages - 1;
        }
        return Math.min(stages - 1, (int) (scale * (stages - 1)));
    }

    /**
     * @apiNote If the data is gaseous then scale is ignored
     */
    public static Model3D getModel(RenderData data, double scale) {
        int maxStages = Math.max(data.height * BLOCK_STAGES, 1);
        int stage;
        if (data.height == 0) {
            //If there is no height set it to 1 for the stage as max stages is going to be one as well
            stage = 1;
        } else if (data.isGaseous()) {
            stage = maxStages;
        } else {
            stage = Math.min(maxStages, (int) (scale * maxStages));
        }
        return cachedCenterData.computeIfAbsent(data, d -> new Int2ObjectOpenHashMap<>())
              .computeIfAbsent(stage, s -> new Model3D()
                    .setTexture(data.getTexture())
                    .xBounds(0.01F, data.length - 0.02F)
                    .yBounds(0.01F, data.height * (s / (float) maxStages) - 0.02F)
                    .zBounds(0.01F, data.width - 0.02F)
              );
    }

    //Undoes the z-fighting height shift from the model
    public static float getActualHeight(Model3D model) {
        return model.maxY + 0.02F;
    }

    @Nullable
    public static Model3D getValveModel(ValveRenderData data, float height) {
        if (switch (data.getSide()) {
            case DOWN -> height >= 0.49F;
            case UP -> height >= data.height;
            default -> height - data.getValveFluidHeight() >= 0.69F;
        }) {
            return null;
        }
        return cachedValveFluids.computeIfAbsent(data, d -> new Float2ObjectOpenHashMap<>())
              .computeIfAbsent(height, fluidHeight -> {
                  Model3D model = new Model3D()
                        .prepFlowing(data.fluidType)
                        .setSideRender(Direction.DOWN, fluidHeight == 0)
                        .xBounds(0.3F, 0.7F)
                        .zBounds(0.3F, 0.7F);
                  Direction side = data.getSide();
                  if (side.getAxis().isHorizontal()) {
                      model.yBounds(fluidHeight - data.getValveFluidHeight() + 0.01F, 0.7F);
                  }
                  return switch (side) {
                      case DOWN -> model.yBounds(fluidHeight + 1.01F, 1.5F);
                      case UP -> model.yBounds(fluidHeight - data.height - 0.01F, -0.01F);
                      case NORTH -> model.zBounds(1.02F, 1.4F);
                      case SOUTH -> model.zBounds(-0.4F, -0.03F);
                      case WEST -> model.xBounds(1.02F, 1.4F);
                      case EAST -> model.xBounds(-0.4F, -0.03F);
                  };
              });
    }

    public static void resetCachedModels() {
        cachedCenterData.clear();
        cachedValveFluids.clear();
    }
}