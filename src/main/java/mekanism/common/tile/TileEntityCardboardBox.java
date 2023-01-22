package mekanism.common.tile;

import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.common.block.BlockCardboardBox.BlockData;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.util.NBTUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class TileEntityCardboardBox extends TileEntityUpdateable {

    public BlockData storedData;

    public TileEntityCardboardBox() {
        super(MekanismTileEntityTypes.CARDBOARD_BOX.getTileEntityType());
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbtTags) {
        super.load(state, nbtTags);
        NBTUtils.setCompoundIfPresent(nbtTags, NBTConstants.DATA, nbt -> storedData = BlockData.read(nbt));
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT nbtTags) {
        super.save(nbtTags);
        if (storedData != null) {
            nbtTags.put(NBTConstants.DATA, storedData.write(new CompoundNBT()));
        }
        return nbtTags;
    }
}