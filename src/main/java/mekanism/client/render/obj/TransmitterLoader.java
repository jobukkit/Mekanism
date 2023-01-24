package mekanism.client.render.obj;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mekanism.api.JsonConstants;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import org.jetbrains.annotations.NotNull;

public class TransmitterLoader implements IGeometryLoader<TransmitterModel> {

    public static final TransmitterLoader INSTANCE = new TransmitterLoader();

    private TransmitterLoader() {
    }

    @NotNull
    @Override
    public TransmitterModel read(@NotNull JsonObject modelContents, @NotNull JsonDeserializationContext deserializationContext) throws JsonParseException {
        //Wrap the Obj loader to read our file
        ObjModel model = ObjLoader.INSTANCE.read(modelContents, deserializationContext);
        ObjModel glass = null;
        if (modelContents.has(JsonConstants.GLASS)) {
            glass = ObjLoader.INSTANCE.read(modelContents.getAsJsonObject(JsonConstants.GLASS), deserializationContext);
        }
        return new TransmitterModel(model, glass);
    }
}