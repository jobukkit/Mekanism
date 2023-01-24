package mekanism.common.registration.impl;

import mekanism.api.providers.IItemProvider;
import mekanism.common.registration.WrappedRegistryObject;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;

public class ItemRegistryObject<ITEM extends Item> extends WrappedRegistryObject<ITEM> implements IItemProvider {

    public ItemRegistryObject(RegistryObject<ITEM> registryObject) {
        super(registryObject);
    }

    @Nonnull
    @Override
    public ITEM getItem() {
        return get();
    }

    /**
     * Do not call, this is only for use in {@link MekanismUtils#isGameStateInvalid()}
     */
    @Deprecated
    public boolean doesItemExist() {
        return registryObject.isPresent();
    }
}