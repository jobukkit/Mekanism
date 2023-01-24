package mekanism.common.content.filter;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IMaterialFilter<FILTER extends IMaterialFilter<FILTER>> extends IFilter<FILTER> {

    @Nonnull
    ItemStack getMaterialItem();

    void setMaterialItem(@Nonnull ItemStack stack);

    @Override
    default boolean hasFilter() {
        return !getMaterialItem().isEmpty();
    }
}