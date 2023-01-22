package mekanism.api.providers;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface IEntityTypeProvider extends IBaseProvider {

    /**
     * Gets the entity type this provider represents.
     */
    @Nonnull
    EntityType<?> getEntityType();

    @Override
    default ResourceLocation getRegistryName() {
        return getEntityType().getRegistryName();
    }

    @Override
    default ITextComponent getTextComponent() {
        return getEntityType().getDescription();
    }

    @Override
    default String getTranslationKey() {
        return getEntityType().getDescriptionId();
    }
}