package mekanism.common.loot.table;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mekanism.api.NBTConstants;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.block.attribute.Attributes.AttributeInventory;
import mekanism.common.block.attribute.Attributes.AttributeRedstone;
import mekanism.common.block.attribute.Attributes.AttributeSecurity;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.ISideConfiguration;
import mekanism.common.tile.interfaces.ISustainedData;
import mekanism.common.util.EnumUtils;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.LootTable.Builder;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
import net.minecraft.world.storage.loot.functions.CopyNbt;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

public abstract class BaseBlockLootTables extends BlockLootTables {

    private final Set<Block> knownBlocks = new ObjectOpenHashSet<>();
    private final Set<Block> toSkip = new ObjectOpenHashSet<>();

    @Override
    protected abstract void addTables();

    @Override
    protected void registerLootTable(@Nonnull Block block, @Nonnull LootTable.Builder table) {
        //Overwrite the core register method to add to our list of known blocks
        super.registerLootTable(block, table);
        knownBlocks.add(block);
    }

    @Nonnull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }

    protected void skip(IBlockProvider... blockProviders) {
        for (IBlockProvider blockProvider : blockProviders) {
            toSkip.add(blockProvider.getBlock());
        }
    }

    protected boolean skipBlock(Block block) {
        //Skip any blocks that we already registered a table for or have marked to skip
        return knownBlocks.contains(block) || toSkip.contains(block);
    }

    protected static LootTable.Builder droppingWithFortuneOrRandomly(Block block, IItemProvider item, IRandomRange range) {
        return droppingWithSilkTouch(block, withExplosionDecay(block, ItemLootEntry.builder(item.asItem()).acceptFunction(SetCount.builder(range))
              .acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE))));
    }

    //IBlockProvider versions of BlockLootTable methods, modified to support varargs
    protected void registerDropSelfLootTable(List<IBlockProvider> blockProviders) {
        for (IBlockProvider blockProvider : blockProviders) {
            Block block = blockProvider.getBlock();
            if (!skipBlock(block)) {
                registerDropSelfLootTable(block);
            }
        }
    }

    protected void registerLootTable(Function<Block, Builder> factory, IBlockProvider... blockProviders) {
        for (IBlockProvider blockProvider : blockProviders) {
            registerLootTable(blockProvider.getBlock(), factory);
        }
    }

    protected void registerDropSelfWithContentsLootTable(List<IBlockProvider> blockProviders) {
        //TODO: See if there is other stuff we want to be transferring which we currently do not
        // For example, when writing this we added dump mode for chemical tanks to getting transferred to the item
        for (IBlockProvider blockProvider : blockProviders) {
            Block block = blockProvider.getBlock();
            if (skipBlock(block)) {
                continue;
            }
            CopyNbt.Builder nbtBuilder = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY);
            boolean hasData = false;
            @Nullable
            TileEntity tile = null;
            if (block instanceof IHasTileEntity) {
                tile = ((IHasTileEntity<?>) block).getTileType().create();
            }
            if (Attribute.has(block, AttributeSecurity.class)) {
                //TODO: Should we just save the entire security component?
                nbtBuilder.replaceOperation(NBTConstants.COMPONENT_SECURITY + "." + NBTConstants.OWNER_UUID, NBTConstants.MEK_DATA + "." + NBTConstants.OWNER_UUID);
                nbtBuilder.replaceOperation(NBTConstants.COMPONENT_SECURITY + "." + NBTConstants.SECURITY_MODE, NBTConstants.MEK_DATA + "." + NBTConstants.SECURITY_MODE);
                hasData = true;
            }
            if (Attribute.has(block, AttributeUpgradeSupport.class)) {
                nbtBuilder.replaceOperation(NBTConstants.COMPONENT_UPGRADE, NBTConstants.MEK_DATA + "." + NBTConstants.COMPONENT_UPGRADE);
                hasData = true;
            }
            if (tile instanceof ISideConfiguration) {
                nbtBuilder.replaceOperation(NBTConstants.COMPONENT_CONFIG, NBTConstants.MEK_DATA + "." + NBTConstants.COMPONENT_CONFIG);
                nbtBuilder.replaceOperation(NBTConstants.COMPONENT_EJECTOR, NBTConstants.MEK_DATA + "." + NBTConstants.COMPONENT_EJECTOR);
                hasData = true;
            }
            if (tile instanceof ISustainedData) {
                Set<Entry<String, String>> remapEntries = ((ISustainedData) tile).getTileDataRemap().entrySet();
                for (Entry<String, String> remapEntry : remapEntries) {
                    nbtBuilder.replaceOperation(remapEntry.getKey(), NBTConstants.MEK_DATA + "." + remapEntry.getValue());
                }
                if (!remapEntries.isEmpty()) {
                    hasData = true;
                }
            }
            if (Attribute.has(block, AttributeRedstone.class)) {
                nbtBuilder.replaceOperation(NBTConstants.CONTROL_TYPE, NBTConstants.MEK_DATA + "." + NBTConstants.CONTROL_TYPE);
                hasData = true;
            }
            if (tile instanceof TileEntityMekanism) {
                TileEntityMekanism tileEntity = (TileEntityMekanism) tile;
                for (SubstanceType type : EnumUtils.SUBSTANCES) {
                    if (tileEntity.handles(type)) {
                        List<? extends INBTSerializable<CompoundNBT>> list = type.getContainers(tileEntity);
                        if (list.size() > 0) {
                            nbtBuilder.replaceOperation(type.getContainerTag(), NBTConstants.MEK_DATA + "." + type.getContainerTag());
                            hasData = true;
                        }
                    }
                }
            }
            if (Attribute.has(block, AttributeInventory.class)) {
                //If the block has an inventory, copy the inventory slots,
                // but if it is an IItemHandler, which for most cases of ours it will be,
                // then only copy the slots if we actually have any slots because otherwise maybe something just went wrong
                if (!(tile instanceof IItemHandler) || ((IItemHandler) tile).getSlots() > 0) {
                    //If we don't actually handle saving an inventory (such as the quantum entangloporter, don't actually add it as something to copy)
                    if (!(tile instanceof TileEntityMekanism) || ((TileEntityMekanism) tile).persistInventory()) {
                        nbtBuilder.replaceOperation(NBTConstants.ITEMS, NBTConstants.MEK_DATA + "." + NBTConstants.ITEMS);
                        hasData = true;
                    }
                }
            }
            if (block instanceof BlockCardboardBox) {
                //TODO: Do this better so that it doesn't have to be as hard coded to being a cardboard box
                nbtBuilder.replaceOperation(NBTConstants.DATA, NBTConstants.MEK_DATA + "." + NBTConstants.DATA);
                hasData = true;
            }
            if (!hasData) {
                //To keep the json as clean as possible don't bother even registering a blank accept function if we have no
                // persistent data that we want to copy. Also log a warning so that we don't have to attempt to check against
                // that block
                registerDropSelfLootTable(block);
            } else {
                registerLootTable(block, LootTable.builder().addLootPool(withSurvivesExplosion(block, LootPool.builder().rolls(ConstantRange.of(1))
                      .addEntry(ItemLootEntry.builder(block).acceptFunction(nbtBuilder)))));
            }
        }
    }
}