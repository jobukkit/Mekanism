package mekanism.api.chemical;

import com.mojang.datafixers.types.Func;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasTags;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfuseTypeTags;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentTags;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChemicalTags<CHEMICAL extends Chemical<CHEMICAL>> {

    public static final ChemicalTags<Gas> GAS = new ChemicalTags<>(ChemicalTags::gasTag, GasTags::getCollection);
    public static final ChemicalTags<InfuseType> INFUSE_TYPE = new ChemicalTags<>(ChemicalTags::infusionTag, InfuseTypeTags::getCollection);
    public static final ChemicalTags<Pigment> PIGMENT = new ChemicalTags<>(ChemicalTags::pigmentTag, PigmentTags::getCollection);
    public static final ChemicalTags<Slurry> SLURRY = new ChemicalTags<>(ChemicalTags::slurryTag, SlurryTags::getCollection);

    private Supplier<TagCollection<CHEMICAL>> collection;

    private Function<ResourceLocation, Tag<CHEMICAL>> tagger;

    private ChemicalTags(Function<ResourceLocation, Tag<CHEMICAL>> taggerIn, Supplier<TagCollection<CHEMICAL>> collectionIn) {
        tagger = taggerIn;
        collection = collectionIn;
    }

    public TagCollection<CHEMICAL> getCollection() {
        return collection.get();
    }

    public ResourceLocation lookupTag(Tag<CHEMICAL> tag) {
        //Manual and slightly modified implementation of TagCollection#func_232975_b_ to have better reverse lookup handling
        TagCollection<CHEMICAL> collection = getCollection();
        ResourceLocation resourceLocation = null;
        //If we failed to get the resource location, try manually looking it up by a "matching" entry
        // as the objects are different and neither Tag nor NamedTag override equals and hashCode
        Collection<CHEMICAL> chemicals = tag.getAllElements();
        for (Map.Entry<ResourceLocation, Tag<CHEMICAL>> entry : collection.getTagMap().entrySet()) {
            if (chemicals.equals(entry.getValue().getAllElements())) {
                resourceLocation = entry.getKey();
                break;
            }
        }
        if (resourceLocation == null) {
            throw new IllegalStateException("Unrecognized tag");
        }
        return resourceLocation;
    }

    public static Tag<Gas> gasTag(ResourceLocation resourceLocation) {
        return new GasTags.Wrapper(resourceLocation);
    }

    public static Tag<InfuseType> infusionTag(ResourceLocation resourceLocation) {
        return new InfuseTypeTags.Wrapper(resourceLocation);
    }

    public static Tag<Pigment> pigmentTag(ResourceLocation resourceLocation) {
        return new PigmentTags.Wrapper(resourceLocation);
    }

    public static Tag<Slurry> slurryTag(ResourceLocation resourceLocation) {
        return new SlurryTags.Wrapper(resourceLocation);
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>> Tag<CHEMICAL> chemicalTag(ResourceLocation resourceLocation, ChemicalTags<CHEMICAL> chemicalTags) {
        return chemicalTags.tagger.apply(resourceLocation);
    }
}