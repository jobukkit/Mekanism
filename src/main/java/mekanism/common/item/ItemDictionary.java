package mekanism.common.item;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.item.DictionaryContainer;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.Set;

public class ItemDictionary extends Item {

    public ItemDictionary(Properties properties) {
        super(properties.maxStackSize(1).rarity(Rarity.UNCOMMON));
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.isSneaking()) {
            World world = context.getWorld();
            if (!world.isRemote) {
                sendTagsToPlayer(player, world.getBlockState(context.getPos()).getBlock().getTags());
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (!world.isRemote()) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack.getDisplayName(), (i, inv, p) -> new DictionaryContainer(i, inv, hand, stack)),
                      buf -> {
                          buf.writeEnumValue(hand);
                          buf.writeItemStack(stack);
                      });
            }
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        } else {
            BlockRayTraceResult result = MekanismUtils.rayTrace(player, FluidMode.ANY);
            if (result.getType() != Type.MISS) {
                Block block = world.getBlockState(result.getPos()).getBlock();
                if (block instanceof FlowingFluidBlock) {
                    if (!world.isRemote()) {
                        sendTagsToPlayer(player, ((FlowingFluidBlock) block).getFluid().getTags());
                    }
                    return new ActionResult<>(ActionResultType.SUCCESS, stack);
                }
            }
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    private void sendTagsToPlayer(PlayerEntity player, Set<ResourceLocation> tags) {
        if (tags.isEmpty()) {
            player.sendMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.GRAY, MekanismLang.DICTIONARY_NO_KEY));
        } else {
            player.sendMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.GRAY, MekanismLang.DICTIONARY_KEYS_FOUND));
            for (ResourceLocation tag : tags) {
                player.sendMessage(MekanismLang.DICTIONARY_KEY.translateColored(EnumColor.DARK_GREEN, tag));
            }
        }
    }
}