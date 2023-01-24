package mekanism.common.registration;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;
import java.util.function.Supplier;

public class WrappedDeferredRegister<T extends IForgeRegistryEntry<T>> {

    protected final DeferredRegister<T> internal;

    protected WrappedDeferredRegister(String modid, IForgeRegistry<T> registry) {
        internal = new DeferredRegister<>(registry, modid);
    }

    /**
     * @apiNote For use with custom registries

    protected WrappedDeferredRegister(String modid, Class<T> base) {
        internal = new DeferredRegister<>(base, modid);
    }*/

    protected <I extends T, W extends WrappedRegistryObject<I>> W register(String name, Supplier<? extends I> sup, Function<RegistryObject<I>, W> objectWrapper) {
        return objectWrapper.apply(internal.register(name, sup));
    }

    /**
     * Only call this from mekanism and for custom registries
     */
    public void createAndRegister(IEventBus bus, String name) {
        register(bus);
    }

    public void register(IEventBus bus) {
        internal.register(bus);
    }
}