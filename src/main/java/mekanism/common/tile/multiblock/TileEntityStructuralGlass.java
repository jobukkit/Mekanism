package mekanism.common.tile.multiblock;

import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;

import java.util.Locale;

public class TileEntityStructuralGlass extends TileEntityStructuralMultiblock {

    public TileEntityStructuralGlass() {
        super(MekanismBlocks.STRUCTURAL_GLASS);
    }

    @Override
    public boolean canInterface(MultiblockManager<?> manager) {
        return !manager.getName().toLowerCase(Locale.ROOT).contains("reactor");
    }
}
