package mekanism.common.config.value;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import mekanism.common.Mekanism;
import mekanism.common.config.IMekanismConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class CachedValue<T> {

    private final IMekanismConfig config;
    protected final ConfigValue<T> internal;
    private Set<IConfigValueInvalidationListener> invalidationListeners;

    protected CachedValue(IMekanismConfig config, ConfigValue<T> internal) {
        this.config = config;
        this.internal = internal;
        this.config.addCachedValue(this);
    }

    public boolean hasInvalidationListeners() {
        return invalidationListeners != null && !invalidationListeners.isEmpty();
    }

    public void addInvalidationListener(IConfigValueInvalidationListener listener) {
        if (invalidationListeners == null) {
            invalidationListeners = new HashSet<>();
        }
        if (!invalidationListeners.add(listener)) {
            Mekanism.logger.warn("Duplicate invalidation listener added");
        }
    }

    public void removeInvalidationListener(IConfigValueInvalidationListener listener) {
        if (invalidationListeners == null) {
            Mekanism.logger.warn("Unable to remove specified invalidation listener, no invalidation listeners have been added.");
        } else if (!invalidationListeners.remove(listener)) {
            Mekanism.logger.warn("Unable to remove specified invalidation listener.");
        }
    }

    public boolean removeInvalidationListenersMatching(Predicate<IConfigValueInvalidationListener> checker) {
        return invalidationListeners != null && !invalidationListeners.isEmpty() && invalidationListeners.removeIf(checker);
    }

    protected abstract boolean clearCachedValue(boolean checkChanged);

    public final void clearCache() {
        if (hasInvalidationListeners()) {
            //Only clear cached values that have invalidation listeners if the config is loaded, as if it isn't loaded then
            // we will fail to clear the cache when we check for if the values have changed. Having a few config values using
            // slightly extra memory, and invalid values shouldn't matter as the config should only be used if it is loaded
            // so once the config is loaded there should be another clearCache call that then causes these values to get updated
            if (isLoaded() && clearCachedValue(true)) {
                invalidationListeners.forEach(IConfigValueInvalidationListener::run);
            }
        } else {
            clearCachedValue(false);
        }
    }

    protected boolean isLoaded() {
        return config.getConfigSpec().isLoaded();
    }

    @FunctionalInterface
    public interface IConfigValueInvalidationListener extends Runnable {
        //Note: If we ever have any invalidation listeners that end up being lazy we can easily add a method to this
        // to specify it and then not bother regrabbing the value instantly
    }
}