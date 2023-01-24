package mekanism.common.capabilities.holder.fluid;

import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.holder.IHolder;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IFluidTankHolder extends IHolder {

    @Nonnull
    List<IExtendedFluidTank> getTanks(@Nullable Direction side);
}