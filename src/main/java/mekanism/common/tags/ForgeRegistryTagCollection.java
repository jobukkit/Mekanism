package mekanism.common.tags;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Builder;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

public class ForgeRegistryTagCollection<T extends IForgeRegistryEntry<T>> extends TagCollection<T> {

    private final IForgeRegistry<T> registry;
    private final Consumer<TagCollection<T>> collectionSetter;

    public ForgeRegistryTagCollection(IForgeRegistry<T> registry, String location, String type, Consumer<TagCollection<T>> collectionSetter) {
        super(key -> Optional.ofNullable(registry.getValue(key)), location, false, type);
        this.registry = registry;
        this.collectionSetter = collectionSetter;
    }

    public void write(PacketBuffer buffer) {
        Map<ResourceLocation, Tag<T>> tagMap = this.getTagMap();
        buffer.writeVarInt(tagMap.size());
        for (Entry<ResourceLocation, Tag<T>> entry : tagMap.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            Tag<T> tag = entry.getValue();
            Collection<T> tags = tag.getAllElements();
            buffer.writeVarInt(tags.size());
            for (T element : tags) {
                ResourceLocation key = this.registry.getKey(element);
                if (key != null) {
                    buffer.writeResourceLocation(key);
                }
            }
        }
    }

    public void read(PacketBuffer buffer) {
        Map<ResourceLocation, Tag<T>> tagMap = new Object2ObjectOpenHashMap<>();
        int tagCount = buffer.readVarInt();
        for (int i = 0; i < tagCount; ++i) {
            ResourceLocation resourceLocation = buffer.readResourceLocation();
            Builder<T> builder = Builder.create();
            int elementCount = buffer.readVarInt();
            for (int j = 0; j < elementCount; ++j) {
                T value = registry.getValue(buffer.readResourceLocation());
                if (value != null) {
                    //Should never be null anyways
                    builder.add(value);
                }
            }
            tagMap.put(resourceLocation, builder.build(resourceLocation));
        }
        toImmutable(tagMap);
    }

    public void setCollection() {
        collectionSetter.accept(this);
    }
}