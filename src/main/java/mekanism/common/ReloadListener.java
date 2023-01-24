package mekanism.common;

import mekanism.common.network.PacketClearRecipeCache;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ReloadListener implements IFutureReloadListener {

    @Nonnull
    @Override
    public CompletableFuture<Void> reload(@Nonnull IStage stage, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler preparationsProfiler,
          @Nonnull IProfiler reloadProfiler, @Nonnull Executor backgroundExecutor, @Nonnull Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            MekanismRecipeType.clearCache();
            Mekanism.packetHandler.sendToAllIfLoaded(new PacketClearRecipeCache());
            CommonWorldTickHandler.flushTagAndRecipeCaches = true;
        }, gameExecutor).thenCompose(stage::markCompleteAwaitingOthers);
    }
}