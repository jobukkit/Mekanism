package mekanism.common.tile.interfaces;

import mekanism.api.Upgrade;
import mekanism.api.Upgrade.IUpgradeInfoHandler;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.Set;

public interface ITileUpgradable extends IUpgradeTile, IUpgradeInfoHandler {

    Set<Upgrade> getSupportedUpgrade();

    @Override
    default List<ITextComponent> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? UpgradeUtils.getExpScaledInfo(this, upgrade) : UpgradeUtils.getMultScaledInfo(this, upgrade);
    }
}