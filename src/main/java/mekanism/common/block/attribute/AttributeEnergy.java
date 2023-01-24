package mekanism.common.block.attribute;

import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;

import javax.annotation.Nonnull;

public class AttributeEnergy implements Attribute {

    private FloatingLongSupplier energyUsage = () -> FloatingLong.ZERO;
    // 2 operations (20 secs) worth of ticks * usage
    private FloatingLongSupplier energyStorage = () -> energyUsage.get().multiply(400);

    public AttributeEnergy(FloatingLongSupplier energyUsage, FloatingLongSupplier energyStorage) {
        if (energyUsage != null) {
            this.energyUsage = energyUsage;
        }
        if (energyStorage != null) {
            this.energyStorage = energyStorage;
        }
    }

    @Nonnull
    public FloatingLong getUsage() {
        return energyUsage.get();
    }

    @Nonnull
    public FloatingLong getConfigStorage() {
        return energyStorage.get();
    }

    @Nonnull
    public FloatingLong getStorage() {
        return getConfigStorage().max(getUsage());
    }
}
