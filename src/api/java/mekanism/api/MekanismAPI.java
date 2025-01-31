package mekanism.api;

import javax.annotation.Nonnull;
import mekanism.api.chemical.gas.EmptyGas;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.EmptyInfuseType;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.EmptyPigment;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.EmptySlurry;
import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MekanismAPI {

    /**
     * The version of the api classes - may not always match the mod's version
     */
    public static final String API_VERSION = "10.0.9";
    public static final String MEKANISM_MODID = "mekanism";
    /**
     * Mekanism debug mode
     */
    public static boolean debug = false;

    public static final Logger logger = LogManager.getLogger(MEKANISM_MODID + "_api");

    private static IForgeRegistry<Gas> GAS_REGISTRY = new RegistryBuilder<Gas>().setName(new ResourceLocation(MEKANISM_MODID, "gas")).setType(Gas.class).create();
    private static IForgeRegistry<InfuseType> INFUSE_TYPE_REGISTRY = new RegistryBuilder<InfuseType>().setName(new ResourceLocation(MEKANISM_MODID, "infuse_type")).setType(InfuseType.class).create();;
    private static IForgeRegistry<Pigment> PIGMENT_REGISTRY = new RegistryBuilder<Pigment>().setName(new ResourceLocation(MEKANISM_MODID, "pigment")).setType(Pigment.class).create();
    private static IForgeRegistry<Slurry> SLURRY_REGISTRY = new RegistryBuilder<Slurry>().setName(new ResourceLocation(MEKANISM_MODID, "slurry")).setType(Slurry.class).create();

    //Note: None of the empty variants support registry replacement
    @Nonnull
    public static final Gas EMPTY_GAS = new EmptyGas();
    @Nonnull
    public static final InfuseType EMPTY_INFUSE_TYPE = new EmptyInfuseType();
    @Nonnull
    public static final Pigment EMPTY_PIGMENT = new EmptyPigment();
    @Nonnull
    public static final Slurry EMPTY_SLURRY = new EmptySlurry();

    @Nonnull
    public static IForgeRegistry<Gas> gasRegistry() {
        if (GAS_REGISTRY == null) {
            GAS_REGISTRY = RegistryManager.ACTIVE.getRegistry(Gas.class);
        }
        return GAS_REGISTRY;
    }

    @Nonnull
    public static IForgeRegistry<InfuseType> infuseTypeRegistry() {
        if (INFUSE_TYPE_REGISTRY == null) {
            INFUSE_TYPE_REGISTRY = RegistryManager.ACTIVE.getRegistry(InfuseType.class);
        }
        return INFUSE_TYPE_REGISTRY;
    }

    @Nonnull
    public static IForgeRegistry<Pigment> pigmentRegistry() {
        if (PIGMENT_REGISTRY == null) {
            PIGMENT_REGISTRY = RegistryManager.ACTIVE.getRegistry(Pigment.class);
        }
        return PIGMENT_REGISTRY;
    }

    @Nonnull
    public static IForgeRegistry<Slurry> slurryRegistry() {
        if (SLURRY_REGISTRY == null) {
            SLURRY_REGISTRY = RegistryManager.ACTIVE.getRegistry(Slurry.class);
        }
        return SLURRY_REGISTRY;
    }
}