package mekanism.common.capabilities.chemical.dynamic;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.pigment.IPigmentTank;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IPigmentTracker extends IContentsListener {

    @Nonnull
    List<IPigmentTank> getPigmentTanks(@Nullable Direction side);
}