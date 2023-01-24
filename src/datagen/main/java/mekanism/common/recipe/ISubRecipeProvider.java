package mekanism.common.recipe;

import net.minecraft.data.IFinishedRecipe;

import java.util.function.Consumer;

/**
 * Interface for helping split the recipe provider over multiple classes to make it a bit easier to interact with
 */
public interface ISubRecipeProvider {

    void addRecipes(Consumer<IFinishedRecipe> consumer);
}