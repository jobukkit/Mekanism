package mekanism.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class PacketLaserHitBlock {

    private final BlockRayTraceResult result;

    public PacketLaserHitBlock(BlockRayTraceResult result) {
        this.result = result;
    }

    public static void handle(PacketLaserHitBlock message, Supplier<Context> context) {
        context.get().enqueueWork(() -> {
            if (Minecraft.getInstance().world != null) {
                Minecraft.getInstance().particles.addBlockHitEffects(message.result.getPos(), message.result);
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketLaserHitBlock pkt, PacketBuffer buf) {
        buf.writeBlockRay(pkt.result);
    }

    public static PacketLaserHitBlock decode(PacketBuffer buf) {
        return new PacketLaserHitBlock(buf.readBlockRay());
    }
}