package mekanism.api.chemical.slurry;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

public class SlurryTags {

    private static TagCollection<Slurry> collection = new TagCollection<>(location -> Optional.empty(), "", false, "");
    private static int generation;

    public static void setCollection(TagCollection<Slurry> collectionIn) {
        collection = collectionIn;
        generation++;
    }

    public static TagCollection<Slurry> getCollection() {
        return collection;
    }

    public static int getGeneration() {
        return generation;
    }

    public static class Wrapper extends Tag<Slurry> {

        private int lastKnownGeneration = -1;
        private Tag<Slurry> cachedTag;

        public Wrapper(ResourceLocation resourceLocation) {
            super(resourceLocation);
        }

        @Override
        public boolean contains(@Nonnull Slurry gas) {
            if (this.lastKnownGeneration != SlurryTags.generation) {
                this.cachedTag = SlurryTags.collection.getOrCreate(this.getId());
                this.lastKnownGeneration = SlurryTags.generation;
            }
            return this.cachedTag.contains(gas);
        }

        @Nonnull
        @Override
        public Collection<Slurry> getAllElements() {
            if (this.lastKnownGeneration != SlurryTags.generation) {
                this.cachedTag = SlurryTags.collection.getOrCreate(this.getId());
                this.lastKnownGeneration = SlurryTags.generation;
            }
            return this.cachedTag.getAllElements();
        }

        @Nonnull
        @Override
        public Collection<ITagEntry<Slurry>> getEntries() {
            if (this.lastKnownGeneration != SlurryTags.generation) {
                this.cachedTag = SlurryTags.collection.getOrCreate(this.getId());
                this.lastKnownGeneration = SlurryTags.generation;
            }
            return this.cachedTag.getEntries();
        }
    }
}