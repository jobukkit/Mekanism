package mekanism.client.render.obj;

import net.minecraft.client.renderer.model.Material;
import net.minecraftforge.client.model.IModelConfiguration;

import javax.annotation.Nonnull;

public class OpaqueModelConfiguration extends WrapperModelConfiguration {

    public OpaqueModelConfiguration(IModelConfiguration internal) {
        super(internal);
    }

    private String adjustTextureName(String name) {
        //Always opaque to ensure that we load the textures regardless
        if (name.startsWith("#side")) {
            return name + "_opaque";
        } else if (name.startsWith("#center")) {
            return name.contains("glass") ? "#center_glass_opaque" : "#center_opaque";
        }
        return name;
    }

    @Override
    public boolean isTexturePresent(@Nonnull String name) {
        return internal.isTexturePresent(adjustTextureName(name));
    }

    @Nonnull
    @Override
    public Material resolveTexture(@Nonnull String name) {
        return internal.resolveTexture(adjustTextureName(name));
    }
}