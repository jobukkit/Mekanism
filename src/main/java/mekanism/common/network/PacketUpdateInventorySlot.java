package mekanism.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class PacketUpdateInventorySlot {

    private final ItemStack containerStack;
    private final int slotId;

    public PacketUpdateInventorySlot(ItemStack containerStack, int slotId) {
        this.containerStack = containerStack;
        this.slotId = slotId;
    }

    public static void handle(PacketUpdateInventorySlot message, Supplier<Context> context) {
        PlayerEntity player = context.get().getSender();
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            player.inventory.setInventorySlotContents(message.slotId, message.containerStack);
            player.inventory.markDirty();
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketUpdateInventorySlot pkt, PacketBuffer buf) {
        buf.writeItemStack(pkt.containerStack);
        buf.writeInt(pkt.slotId);
    }

    public static PacketUpdateInventorySlot decode(PacketBuffer buf) {
        return new PacketUpdateInventorySlot(buf.readItemStack(), buf.readInt());
    }
}
