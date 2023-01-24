package mekanism.client.render.data;

import mekanism.api.chemical.gas.GasStack;

import javax.annotation.Nonnull;

public class GasRenderData extends ChemicalRenderData<GasStack> {

    public GasRenderData(@Nonnull GasStack chemicalType) {
        super(chemicalType);
    }

    @Override
    public boolean isGaseous() {
        return true;
    }

    @Override
    public boolean equals(Object data) {
        return super.equals(data) && data instanceof GasRenderData && chemicalType.isTypeEqual(((GasRenderData) data).chemicalType);
    }
}