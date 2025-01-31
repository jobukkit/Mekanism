package mekanism.client.render.data;

import mekanism.api.chemical.pigment.PigmentStack;

import javax.annotation.Nonnull;

public class PigmentRenderData extends ChemicalRenderData<PigmentStack> {

    public PigmentRenderData(@Nonnull PigmentStack chemicalType) {
        super(chemicalType);
    }

    @Override
    public boolean isGaseous() {
        return false;
    }

    @Override
    public boolean equals(Object data) {
        return super.equals(data) && data instanceof PigmentRenderData && chemicalType.isTypeEqual(((PigmentRenderData) data).chemicalType);
    }
}