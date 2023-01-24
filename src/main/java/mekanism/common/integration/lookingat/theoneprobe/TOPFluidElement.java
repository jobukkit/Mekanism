package mekanism.common.integration.lookingat.theoneprobe;

import mcjty.theoneprobe.api.IElementNew;
import mekanism.common.integration.lookingat.FluidElement;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class TOPFluidElement extends FluidElement implements IElementNew {

    public TOPFluidElement(@Nonnull FluidStack stored, int capacity) {
        super(stored, capacity);
    }

    public TOPFluidElement(PacketBuffer buf) {
        this(buf.readFluidStack(), buf.readVarInt());
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeFluidStack(stored);
        buf.writeVarInt(capacity);
    }

    @Override
    public int getID() {
        return TOPProvider.FLUID_ELEMENT_ID;
    }
}