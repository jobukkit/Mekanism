package mekanism.common.tile.interfaces;

import mekanism.common.util.MekanismUtils;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface ITileDirectional {

    default boolean isDirectional() {
        return true;
    }

    void setFacing(@Nonnull Direction direction);

    @Nonnull
    Direction getDirection();

    @Nonnull
    default Direction getOppositeDirection() {
        return getDirection().getOpposite();
    }

    @Nonnull
    default Direction getRightSide() {
        return MekanismUtils.getRight(getDirection());
    }

    @Nonnull
    default Direction getLeftSide() {
        return MekanismUtils.getLeft(getDirection());
    }
}