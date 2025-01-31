package mekanism.client.render.data;

import mekanism.api.chemical.slurry.SlurryStack;

import javax.annotation.Nonnull;

public class SlurryRenderData extends ChemicalRenderData<SlurryStack> {

    public SlurryRenderData(@Nonnull SlurryStack chemicalType) {
        super(chemicalType);
    }

    @Override
    public boolean isGaseous() {
        return false;
    }

    @Override
    public boolean equals(Object data) {
        return super.equals(data) && data instanceof SlurryRenderData && chemicalType.isTypeEqual(((SlurryRenderData) data).chemicalType);
    }
}