package mekanism.additions.common.block.plastic;

import mekanism.api.providers.IBlockProvider;
import mekanism.api.text.EnumColor;
import mekanism.common.block.interfaces.IColoredBlock;
import net.minecraft.block.StairsBlock;
import net.minecraftforge.common.ToolType;

public class BlockPlasticGlowStairs extends StairsBlock implements IColoredBlock {

    private final EnumColor color;

    public BlockPlasticGlowStairs(IBlockProvider blockProvider, EnumColor color) {
        super(() -> blockProvider.getBlock().getDefaultState(), Properties.create(BlockPlastic.PLASTIC, color.getMapColor())
              .hardnessAndResistance(5F, 10F).lightValue(10).harvestTool(ToolType.PICKAXE));
        this.color = color;
    }

    @Override
    public EnumColor getColor() {
        return color;
    }
}