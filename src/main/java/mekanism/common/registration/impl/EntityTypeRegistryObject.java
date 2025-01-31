package mekanism.common.registration.impl;

import mekanism.api.providers.IEntityTypeProvider;
import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;

public class EntityTypeRegistryObject<ENTITY extends Entity> extends WrappedRegistryObject<EntityType<ENTITY>> implements IEntityTypeProvider {

    public EntityTypeRegistryObject(RegistryObject<EntityType<ENTITY>> registryObject) {
        super(registryObject);
    }

    @Nonnull
    @Override
    public EntityType<ENTITY> getEntityType() {
        return get();
    }
}