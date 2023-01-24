package mekanism.common.tile.prefab;

import mekanism.api.NBTConstants;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.NBTUtils;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.UUID;

//TODO - V11: Evaluate making neighbor updates to this (and other "non internal" internal multiblocks like induction cells) cause the multiblock to unform
public class TileEntityInternalMultiblock extends TileEntityMekanism {

    protected UUID multiblockUUID;

    public TileEntityInternalMultiblock(IBlockProvider blockProvider) {
        super(blockProvider);
    }

    public void setMultiblock(UUID id) {
        multiblockUUID = id;
    }

    public UUID getMultiblock() {
        return multiblockUUID;
    }

    @Nonnull
    @Override
    public CompoundNBT getReducedUpdateTag() {
        CompoundNBT updateTag = super.getReducedUpdateTag();
        if (multiblockUUID != null) {
            updateTag.putUniqueId(NBTConstants.INVENTORY_ID, multiblockUUID);
        }
        return updateTag;
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundNBT tag) {
        super.handleUpdateTag(tag);
        NBTUtils.setUUIDIfPresentElse(tag, NBTConstants.INVENTORY_ID, this::setMultiblock, () -> multiblockUUID = null);
    }
}