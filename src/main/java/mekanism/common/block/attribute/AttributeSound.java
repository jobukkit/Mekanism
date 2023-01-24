package mekanism.common.block.attribute;

import mekanism.common.base.HolidayManager;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;

public class AttributeSound implements Attribute {

    private final SoundEventRegistryObject<SoundEvent> soundRegistrar;

    public AttributeSound(SoundEventRegistryObject<SoundEvent> soundRegistrar) {
        this.soundRegistrar = soundRegistrar;
    }

    @Nonnull
    public SoundEvent getSoundEvent() {
        return HolidayManager.filterSound(soundRegistrar).get();
    }
}