package mekanism.common.registration.impl;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfuseTypeBuilder;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class InfuseTypeDeferredRegister extends WrappedDeferredRegister<InfuseType> {

    public InfuseTypeDeferredRegister(String modid) {
        super(modid, MekanismAPI.infuseTypeRegistry());
    }

    public InfuseTypeRegistryObject<InfuseType> register(String name, int tint) {
        return register(name, () -> new InfuseType(InfuseTypeBuilder.builder().color(tint)));
    }

    public InfuseTypeRegistryObject<InfuseType> register(String name, ResourceLocation texture) {
        return register(name, () -> new InfuseType(InfuseTypeBuilder.builder(texture)));
    }

    public <INFUSE_TYPE extends InfuseType> InfuseTypeRegistryObject<INFUSE_TYPE> register(String name, Supplier<? extends INFUSE_TYPE> sup) {
        return register(name, sup, InfuseTypeRegistryObject::new);
    }
}