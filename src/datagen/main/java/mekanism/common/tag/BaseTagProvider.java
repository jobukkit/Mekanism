package mekanism.common.tag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.swing.text.html.HTML;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.datagen.tag.ForgeRegistryTagBuilder;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IChemicalProvider;
import mekanism.api.providers.IEntityTypeProvider;
import mekanism.api.providers.IGasProvider;
import mekanism.api.providers.IInfuseTypeProvider;
import mekanism.api.providers.IItemProvider;
import mekanism.api.providers.IPigmentProvider;
import mekanism.api.providers.ISlurryProvider;
import mekanism.common.DataGenJsonConstants;
import mekanism.common.registration.impl.FluidRegistryObject;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.Tag;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseTagProvider implements IDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<TagType<?>, TagTypeMap<?>> supportedTagTypes = new Object2ObjectLinkedOpenHashMap<>();
    private final DataGenerator gen;
    private final String modid;

    protected BaseTagProvider(DataGenerator gen, String modid) {
        this.gen = gen;
        this.modid = modid;
        addTagType(TagType.ITEM);
        addTagType(TagType.BLOCK);
        addTagType(TagType.ENTITY_TYPE);
        addTagType(TagType.FLUID);
        addTagType(TagType.GAS);
        addTagType(TagType.INFUSE_TYPE);
        addTagType(TagType.PIGMENT);
        addTagType(TagType.SLURRY);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tags: " + modid;
    }

    //Protected to allow for extensions to add their own supported types if they have one
    protected <TYPE extends IForgeRegistryEntry<TYPE>> void addTagType(TagType<TYPE> tagType) {
        supportedTagTypes.putIfAbsent(tagType, new TagTypeMap<>(tagType));
    }

    protected abstract void registerTags();

    @Override
    public void act(@Nonnull DirectoryCache cache) {
        supportedTagTypes.values().forEach(TagTypeMap::clear);
        registerTags();
        supportedTagTypes.values().forEach(tagTypeMap -> act(cache, tagTypeMap));
    }

    @SuppressWarnings("UnstableApiUsage")
    private <TYPE extends IForgeRegistryEntry<TYPE>> void act(@Nonnull DirectoryCache cache, TagTypeMap<TYPE> tagTypeMap) {
        if (!tagTypeMap.isEmpty()) {
            String tagTypePath = tagTypeMap.getTagType().getPath();
            tagTypeMap.getBuilders().forEach((id, tagBuilder) -> {
                Path path = gen.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/" + tagTypePath + "/" + id.getPath() + ".json");
                try {
                    String json = GSON.toJson(cleanJsonTag(tagBuilder.serialize()));
                    String hash = HASH_FUNCTION.hashUnencodedChars(json).toString();
                    if (!Objects.equals(cache.getPreviousHash(path), hash) || !Files.exists(path)) {
                        Files.createDirectories(path.getParent());
                        try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                            bufferedwriter.write(json);
                        }
                    }
                    cache.recordHash(path, hash);
                } catch (IOException exception) {
                    LOGGER.error("Couldn't save tags to {}", path, exception);
                }
            });
        }
    }

    private JsonObject cleanJsonTag(JsonObject tagAsJson) {
        if (tagAsJson.has(DataGenJsonConstants.REPLACE)) {
            //Strip out the optional "replace" entry from the tag if it is the default value
            JsonPrimitive replace = tagAsJson.getAsJsonPrimitive(DataGenJsonConstants.REPLACE);
            if (replace.isBoolean() && !replace.getAsBoolean()) {
                tagAsJson.remove(DataGenJsonConstants.REPLACE);
            }
        }
        return tagAsJson;
    }

    //Protected to allow for extensions to add retrieve their own supported types if they have any
    protected <TYPE extends IForgeRegistryEntry<TYPE>> TagTypeMap<TYPE> getTagTypeMap(TagType<TYPE> tagType) {
        return (TagTypeMap<TYPE>) supportedTagTypes.get(tagType);
    }

    protected <TYPE extends IForgeRegistryEntry<TYPE>> Tag.Builder<TYPE>  getBuilder(TagType<TYPE> tagType, Tag<TYPE> tag) {
        return getTagTypeMap(tagType).getBuilder(tag, modid);
    }

    protected Tag.Builder<Item> getItemBuilder(Tag<Item> tag) {
        return getBuilder(TagType.ITEM, tag);
    }

    protected ForgeRegistryTagBuilder<Block> getBlockBuilder(Tag<Block> tag) {
        return getBuilder(TagType.BLOCK, tag);
    }

    protected ForgeRegistryTagBuilder<EntityType<?>> getEntityTypeBuilder(Tag<EntityType<?>> tag) {
        return getBuilder(TagType.ENTITY_TYPE, tag);
    }

    protected ForgeRegistryTagBuilder<Fluid> getFluidBuilder(Tag<Fluid> tag) {
        return getBuilder(TagType.FLUID, tag);
    }

    protected ForgeRegistryTagBuilder<Gas> getGasBuilder(Tag<Gas> tag) {
        return getBuilder(TagType.GAS, tag);
    }

    protected  Tag.Builder<InfuseType> getInfuseTypeBuilder(Tag<InfuseType> tag) {
        return getBuilder(TagType.INFUSE_TYPE, tag);
    }

    protected  Tag.Builder<Slurry> getPigmentBuilder(Tag<Pigment> tag) {
        return getBuilder(TagType.PIGMENT, tag);
    }

    protected Tag.Builder<Slurry> getSlurryBuilder(Tag<Slurry> tag) {
        return getBuilder(TagType.SLURRY, tag);
    }

    @SafeVarargs
    protected final <CHEMICAL extends Chemical<CHEMICAL>> void addToTag(ForgeRegistryTagBuilder<CHEMICAL> tagBuilder, IChemicalProvider<CHEMICAL>... providers) {
        for (IChemicalProvider<CHEMICAL> provider : providers) {
            tagBuilder.add(provider.getChemical());
        }
    }
}