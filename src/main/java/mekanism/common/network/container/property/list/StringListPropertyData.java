package mekanism.common.network.container.property.list;

import mekanism.common.network.BasePacketHandler;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class StringListPropertyData extends ListPropertyData<String> {

    public StringListPropertyData(short property, @Nonnull List<String> values) {
        super(property, ListType.STRING, values);
    }

    public static StringListPropertyData read(short property, int elements, PacketBuffer buffer) {
        List<String> values = new ArrayList<>(elements);
        for (int i = 0; i < elements; i++) {
            values.add(BasePacketHandler.readString(buffer));
        }
        return new StringListPropertyData(property, values);
    }


    @Override
    protected void writeListElements(PacketBuffer buffer) {
        for (String value : values) {
            buffer.writeString(value);
        }
    }
}