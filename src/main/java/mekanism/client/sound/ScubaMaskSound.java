package mekanism.client.sound;

import mekanism.client.ClientTickHandler;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public class ScubaMaskSound extends PlayerSound {

    public ScubaMaskSound(@Nonnull PlayerEntity player) {
        super(player, MekanismSounds.SCUBA_MASK.getSoundEvent());
    }

    @Override
    public boolean shouldPlaySound(@Nonnull PlayerEntity player) {
        return ClientTickHandler.isScubaMaskOn(player);
    }
}