package mekanism.client.sound;

import mekanism.client.ClientTickHandler;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public class GravitationalModulationSound extends PlayerSound {

    public GravitationalModulationSound(@Nonnull PlayerEntity player) {
        super(player, MekanismSounds.GRAVITATIONAL_MODULATION_UNIT.getSoundEvent());
    }

    @Override
    public boolean shouldPlaySound(@Nonnull PlayerEntity player) {
        return ClientTickHandler.isGravitationalModulationOn(player);
    }
}
