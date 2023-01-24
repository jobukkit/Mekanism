package mekanism.common.tile.component.config.slot;

import mekanism.api.energy.IEnergyContainer;

import java.util.Arrays;
import java.util.List;

public class EnergySlotInfo extends BaseSlotInfo {

    private final List<IEnergyContainer> containers;

    public EnergySlotInfo(boolean canInput, boolean canOutput, IEnergyContainer... containers) {
        this(canInput, canOutput, Arrays.asList(containers));
    }

    public EnergySlotInfo(boolean canInput, boolean canOutput, List<IEnergyContainer> containers) {
        super(canInput, canOutput);
        this.containers = containers;
    }

    public List<IEnergyContainer> getContainers() {
        return containers;
    }
}