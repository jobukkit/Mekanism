package mekanism.common.inventory.container.sync;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import mekanism.common.network.to_client.container.property.IntPropertyData;
import org.jetbrains.annotations.NotNull;

/**
 * Version of {@link net.minecraft.world.inventory.DataSlot} for making it easier to handle enums
 */
public class SyncableEnum<ENUM extends Enum<ENUM>> implements ISyncableData {

    public static <ENUM extends Enum<ENUM>> SyncableEnum<ENUM> create(Int2ObjectFunction<ENUM> decoder, @NotNull ENUM defaultValue, Supplier<@NotNull ENUM> getter,
          Consumer<@NotNull ENUM> setter) {
        return new SyncableEnum<>(decoder, defaultValue, getter, setter);
    }

    private final Int2ObjectFunction<ENUM> decoder;
    private final Supplier<@NotNull ENUM> getter;
    private final Consumer<@NotNull ENUM> setter;
    @NotNull
    private ENUM lastKnownValue;

    private SyncableEnum(Int2ObjectFunction<ENUM> decoder, @NotNull ENUM defaultValue, Supplier<@NotNull ENUM> getter, Consumer<@NotNull ENUM> setter) {
        this.decoder = decoder;
        this.lastKnownValue = defaultValue;
        this.getter = getter;
        this.setter = setter;
    }

    @NotNull
    public ENUM get() {
        return getter.get();
    }

    public void set(int ordinal) {
        set(decoder.apply(ordinal));
    }

    public void set(@NotNull ENUM value) {
        setter.accept(value);
    }

    @Override
    public DirtyType isDirty() {
        ENUM oldValue = get();
        boolean dirty = oldValue != this.lastKnownValue;
        this.lastKnownValue = oldValue;
        return DirtyType.get(dirty);
    }

    @Override
    public IntPropertyData getPropertyData(short property, DirtyType dirtyType) {
        return new IntPropertyData(property, get().ordinal());
    }
}