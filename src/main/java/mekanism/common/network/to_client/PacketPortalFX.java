package mekanism.common.network.to_client;

import mekanism.common.network.IMekanismPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketPortalFX implements IMekanismPacket {

    private final BlockPos pos;
    private final Direction direction;

    public PacketPortalFX(BlockPos pos) {
        this(pos, Direction.UP);
    }

    public PacketPortalFX(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ClientWorld world = Minecraft.getInstance().level;
        if (world != null) {
            BlockPos secondPos = pos.relative(direction);
            for (int i = 0; i < 50; i++) {
                world.addParticle(ParticleTypes.PORTAL, pos.getX() + world.random.nextFloat(), pos.getY() + world.random.nextFloat(),
                      pos.getZ() + world.random.nextFloat(), 0.0F, 0.0F, 0.0F);
                world.addParticle(ParticleTypes.PORTAL, secondPos.getX() + world.random.nextFloat(), secondPos.getY() + world.random.nextFloat(),
                      secondPos.getZ() + world.random.nextFloat(), 0.0F, 0.0F, 0.0F);
            }
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(direction);
    }

    public static PacketPortalFX decode(PacketBuffer buffer) {
        return new PacketPortalFX(buffer.readBlockPos(), buffer.readEnum(Direction.class));
    }
}