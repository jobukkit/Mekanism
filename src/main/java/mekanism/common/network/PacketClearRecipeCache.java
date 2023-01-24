package mekanism.common.network;

import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class PacketClearRecipeCache {

    public static void handle(PacketClearRecipeCache message, Supplier<Context> context) {
        context.get().enqueueWork(MekanismRecipeType::clearCache);
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketClearRecipeCache pkt, PacketBuffer buf) {
    }

    public static PacketClearRecipeCache decode(PacketBuffer buf) {
        return new PacketClearRecipeCache();
    }
}