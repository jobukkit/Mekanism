package mekanism.generators.common;

import mekanism.common.MekanismDataGenerator;
import mekanism.generators.client.GeneratorsBlockStateProvider;
import mekanism.generators.client.GeneratorsItemModelProvider;
import mekanism.generators.client.GeneratorsLangProvider;
import mekanism.generators.client.GeneratorsSoundProvider;
import mekanism.generators.common.loot.GeneratorsLootProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = MekanismGenerators.MODID, bus = Bus.MOD)
public class GeneratorsDataGenerator {

    private GeneratorsDataGenerator() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        MekanismDataGenerator.bootstrapConfigs(MekanismGenerators.MODID);
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        //Client side data generators
        gen.addProvider(event.includeClient(), new GeneratorsLangProvider(gen));
        gen.addProvider(event.includeClient(), new GeneratorsSoundProvider(gen, existingFileHelper));
        //Let the blockstate provider see models generated by the item model provider
        GeneratorsItemModelProvider itemModelProvider = new GeneratorsItemModelProvider(gen, existingFileHelper);
        gen.addProvider(event.includeClient(), itemModelProvider);
        gen.addProvider(event.includeClient(), new GeneratorsBlockStateProvider(gen, itemModelProvider.existingFileHelper));
        //Server side data generators
        gen.addProvider(event.includeServer(), new GeneratorsTagProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new GeneratorsLootProvider(gen));
        gen.addProvider(event.includeServer(), new GeneratorsRecipeProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new GeneratorsAdvancementProvider(gen, existingFileHelper));
    }
}