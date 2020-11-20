package mekanism.common.block;

import mekanism.api.text.ILangEntry;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.resource.OreType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;

public class BlockOre extends Block implements IHasDescription {

    private final OreType ore;

    public BlockOre(OreType ore) {
        super(Block.Properties.create(Material.ROCK).hardnessAndResistance(3F, 5F).harvestTool(ToolType.PICKAXE).harvestLevel(1));
        this.ore = ore;
    }

    @Nonnull
    @Override
    public ILangEntry getDescription() {
        return () -> "description.mekanism." + ore.getResource().getRegistrySuffix() + "_ore";
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silkTouch) {
        if (ore.getMaxExp() > 0 && silkTouch == 0) {
            return MathHelper.nextInt(RANDOM, ore.getMinExp(), ore.getMaxExp());
        }
        return super.getExpDrop(state, reader, pos, fortune, silkTouch);
    }
}