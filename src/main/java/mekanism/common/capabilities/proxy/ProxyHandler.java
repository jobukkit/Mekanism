package mekanism.common.capabilities.proxy;

import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.common.capabilities.holder.IHolder;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

@FieldsAreNonnullByDefault
public class ProxyHandler {

    private static final BooleanSupplier alwaysFalse = () -> false;

    @Nullable
    protected final Direction side;
    protected final boolean readOnly;
    protected final BooleanSupplier readOnlyInsert;
    protected final BooleanSupplier readOnlyExtract;

    protected ProxyHandler(@Nullable Direction side, @Nullable IHolder holder) {
        this.side = side;
        this.readOnly = this.side == null;
        this.readOnlyInsert = holder == null ? alwaysFalse : () -> !holder.canInsert(side);
        this.readOnlyExtract = holder == null ? alwaysFalse : () -> !holder.canExtract(side);
    }
}