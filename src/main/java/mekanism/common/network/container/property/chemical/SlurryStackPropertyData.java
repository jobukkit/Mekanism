package mekanism.common.network.container.property.chemical;

import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.network.container.property.PropertyType;

import javax.annotation.Nonnull;

public class SlurryStackPropertyData extends ChemicalStackPropertyData<SlurryStack> {

    public SlurryStackPropertyData(short property, @Nonnull SlurryStack value) {
        super(PropertyType.SLURRY_STACK, property, value);
    }
}