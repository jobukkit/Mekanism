package mekanism.common.capabilities.chemical.dynamic;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.slurry.ISlurryTank;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface ISlurryTracker extends IContentsListener {

    @Nonnull
    List<ISlurryTank> getSlurryTanks(@Nullable Direction side);
}