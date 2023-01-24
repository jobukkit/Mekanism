package mekanism.common.network.container.property.chemical;

import mekanism.api.chemical.gas.GasStack;
import mekanism.common.network.container.property.PropertyType;

import javax.annotation.Nonnull;

public class GasStackPropertyData extends ChemicalStackPropertyData<GasStack> {

    public GasStackPropertyData(short property, @Nonnull GasStack value) {
        super(PropertyType.GAS_STACK, property, value);
    }
}