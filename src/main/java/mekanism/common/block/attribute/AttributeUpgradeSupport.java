package mekanism.common.block.attribute;

import mekanism.api.Upgrade;

import javax.annotation.Nonnull;
import java.util.Set;

public class AttributeUpgradeSupport implements Attribute {

    private final Set<Upgrade> supportedUpgrades;

    public AttributeUpgradeSupport(Set<Upgrade> supportedUpgrades) {
        this.supportedUpgrades = supportedUpgrades;
    }

    @Nonnull
    public Set<Upgrade> getSupportedUpgrades() {
        return supportedUpgrades;
    }
}
