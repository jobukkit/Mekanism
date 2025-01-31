package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;

public class PlacementRegistryObject<CONFIG extends IPlacementConfig, PLACEMENT extends Placement<CONFIG>> extends WrappedRegistryObject<PLACEMENT> {

    public PlacementRegistryObject(RegistryObject<PLACEMENT> registryObject) {
        super(registryObject);
    }

    @Nonnull
    public PLACEMENT getPlacement() {
        return get();
    }
}