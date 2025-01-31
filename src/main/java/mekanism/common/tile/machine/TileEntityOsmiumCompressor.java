package mekanism.common.tile.machine;

import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;

import javax.annotation.Nonnull;

public class TileEntityOsmiumCompressor extends TileEntityAdvancedElectricMachine {

    public TileEntityOsmiumCompressor() {
        super(MekanismBlocks.OSMIUM_COMPRESSOR, BASE_TICKS_REQUIRED);
    }

    @Nonnull
    @Override
    public MekanismRecipeType<ItemStackGasToItemStackRecipe> getRecipeType() {
        return MekanismRecipeType.COMPRESSING;
    }
}