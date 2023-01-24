package mekanism.common.network.container.property.chemical;

import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.network.container.property.PropertyType;

import javax.annotation.Nonnull;

public class InfusionStackPropertyData extends ChemicalStackPropertyData<InfusionStack> {

    public InfusionStackPropertyData(short property, @Nonnull InfusionStack value) {
        super(PropertyType.INFUSION_STACK, property, value);
    }
}