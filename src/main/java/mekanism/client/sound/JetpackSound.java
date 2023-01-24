package mekanism.client.sound;

import mekanism.client.ClientTickHandler;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public class JetpackSound extends PlayerSound {

    public JetpackSound(@Nonnull PlayerEntity player) {
        super(player, MekanismSounds.JETPACK.getSoundEvent());
    }

    @Override
    public boolean shouldPlaySound(@Nonnull PlayerEntity player) {
        return ClientTickHandler.isJetpackActive(player);
    }
}