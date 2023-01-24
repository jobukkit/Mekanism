package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;

public class IRecipeSerializerRegistryObject<RECIPE extends IRecipe<?>> extends WrappedRegistryObject<IRecipeSerializer<RECIPE>> {

    public IRecipeSerializerRegistryObject(RegistryObject<IRecipeSerializer<RECIPE>> registryObject) {
        super(registryObject);
    }

    @Nonnull
    public IRecipeSerializer<RECIPE> getRecipeSerializer() {
        return get();
    }
}