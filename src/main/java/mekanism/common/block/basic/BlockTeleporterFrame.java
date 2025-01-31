package mekanism.common.block.basic;

import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.interfaces.IHasDescription;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

public class BlockTeleporterFrame extends BlockMekanism implements IHasDescription {

    public BlockTeleporterFrame() {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(5F, 10F).lightValue(10));
    }

    @Nonnull
    @Override
    public ILangEntry getDescription() {
        return MekanismLang.DESCRIPTION_TELEPORTER_FRAME;
    }
}