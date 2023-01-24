package mekanism.common.item.interfaces;

import mekanism.client.render.armor.CustomArmor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public interface ISpecialGear {

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    CustomArmor getGearModel();
}