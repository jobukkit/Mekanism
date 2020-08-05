package mekanism.api.chemical.pigment;

import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class PigmentTags {

    private static TagCollection<Pigment> collection = new TagCollection<>(location -> Optional.empty(), "", false, "");
    private static int generation;

    public static void setCollection(TagCollection<Pigment> collectionIn) {
        collection = collectionIn;
        generation++;
    }

    public static TagCollection<Pigment> getCollection() {
        return collection;
    }

    public static int getGeneration() {
        return generation;
    }

    public static class Wrapper extends Tag<Pigment> {

        private int lastKnownGeneration = -1;
        private Tag<Pigment> cachedTag;

        public Wrapper(ResourceLocation resourceLocation) {
            super(resourceLocation);
        }

        @Override
        public boolean contains(@Nonnull Pigment gas) {
            if (this.lastKnownGeneration != PigmentTags.generation) {
                this.cachedTag = PigmentTags.collection.getOrCreate(this.getId());
                this.lastKnownGeneration = PigmentTags.generation;
            }
            return this.cachedTag.contains(gas);
        }

        @Nonnull
        @Override
        public Collection<Pigment> getAllElements() {
            if (this.lastKnownGeneration != mekanism.api.chemical.pigment.PigmentTags.generation) {
                this.cachedTag = mekanism.api.chemical.pigment.PigmentTags.collection.getOrCreate(this.getId());
                this.lastKnownGeneration = mekanism.api.chemical.pigment.PigmentTags.generation;
            }
            return this.cachedTag.getAllElements();
        }

        @Nonnull
        @Override
        public Collection<ITagEntry<Pigment>> getEntries() {
            if (this.lastKnownGeneration != mekanism.api.chemical.pigment.PigmentTags.generation) {
                this.cachedTag = mekanism.api.chemical.pigment.PigmentTags.collection.getOrCreate(this.getId());
                this.lastKnownGeneration = mekanism.api.chemical.pigment.PigmentTags.generation;
            }
            return this.cachedTag.getEntries();
        }
    }
}