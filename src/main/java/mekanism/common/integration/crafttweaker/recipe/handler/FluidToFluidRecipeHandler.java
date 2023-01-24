package mekanism.common.integration.crafttweaker.recipe.handler;

import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import java.util.Optional;
import mekanism.api.recipes.FluidToFluidRecipe;
import mekanism.common.integration.crafttweaker.CrTRecipeComponents;
import mekanism.common.integration.crafttweaker.recipe.manager.FluidToFluidRecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

@IRecipeHandler.For(FluidToFluidRecipe.class)
public class FluidToFluidRecipeHandler extends MekanismRecipeHandler<FluidToFluidRecipe> {

    @Override
    public String dumpToCommandString(IRecipeManager<? super FluidToFluidRecipe> manager, FluidToFluidRecipe recipe) {
        return buildCommandString(manager, recipe, recipe.getInput(), recipe.getOutputDefinition());
    }

    @Override
    public <U extends Recipe<?>> boolean doesConflict(IRecipeManager<? super FluidToFluidRecipe> manager, FluidToFluidRecipe recipe, U o) {
        //Only support if the other is a fluid to fluid recipe and don't bother checking the reverse as the recipe type's generics
        // ensures that it is of the same type
        return o instanceof FluidToFluidRecipe other && ingredientConflicts(recipe.getInput(), other.getInput());
    }

    @Override
    public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super FluidToFluidRecipe> manager, FluidToFluidRecipe recipe) {
        return decompose(recipe.getInput(), recipe.getOutputDefinition());
    }

    @Override
    public Optional<FluidToFluidRecipe> recompose(IRecipeManager<? super FluidToFluidRecipe> m, ResourceLocation name, IDecomposedRecipe recipe) {
        if (m instanceof FluidToFluidRecipeManager manager) {
            return Optional.of(manager.makeRecipe(name,
                  recipe.getOrThrowSingle(CrTRecipeComponents.FLUID.input()),
                  recipe.getOrThrowSingle(CrTRecipeComponents.FLUID.output())
            ));
        }
        return Optional.empty();
    }
}