package mekanism.common.content.filter;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemStackFilter<FILTER extends IItemStackFilter<FILTER>> extends IFilter<FILTER> {

    @Nonnull
    ItemStack getItemStack();

    void setItemStack(@Nonnull ItemStack stack);

    @Override
    default boolean hasFilter() {
        return !getItemStack().isEmpty();
    }
}