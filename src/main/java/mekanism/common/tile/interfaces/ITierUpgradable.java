package mekanism.common.tile.interfaces;

import mekanism.common.upgrade.IUpgradeData;

import javax.annotation.Nullable;

public interface ITierUpgradable {

    boolean canBeUpgraded();

    /**
     * @return The upgrade data for this block or null if something went wrong
     */
    @Nullable
    default IUpgradeData getUpgradeData() {
        return null;
    }
}