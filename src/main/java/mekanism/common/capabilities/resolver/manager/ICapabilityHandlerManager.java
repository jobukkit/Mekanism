package mekanism.common.capabilities.resolver.manager;

import mcp.MethodsReturnNonnullByDefault;
import mekanism.common.capabilities.resolver.ICapabilityResolver;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.List;

@MethodsReturnNonnullByDefault
public interface ICapabilityHandlerManager<CONTAINER> extends ICapabilityResolver {

    /**
     * Checks if the capability handler manager can handle this substance type.
     *
     * @return {@code true} if it can handle the substance type, {@code false} otherwise.
     */
    boolean canHandle();

    /**
     * Gets the containers for a given side.
     *
     * @param side The side
     *
     * @return Containers on the given side
     */
    List<CONTAINER> getContainers(@Nullable Direction side);
}