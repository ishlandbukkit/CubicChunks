package io.github.opencubicchunks.cubicchunks.mixin.debug.common;

import java.util.Iterator;
import java.util.Map;

import io.github.opencubicchunks.cubicchunks.server.ICubicWorld;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 999)
public abstract class MixinMinecraftServer {
    @Shadow @Final private static Logger LOGGER;

    @Shadow private long nextTickTime;

    @Shadow @Final private Map<ResourceKey<Level>, ServerLevel> levels;


    @Shadow public abstract ServerLevel overworld();

    @Shadow protected abstract void waitUntilNextTick();

    @Shadow protected abstract void updateMobSpawningFlags();

    /**
     * @author NotStirred
     * @reason Custom chunk loading order for debugging
     */
    @Inject(method = "prepareLevels", at = @At("HEAD"), cancellable = true)
    private void prepareLevels(ChunkProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        ci.cancel();
        ServerLevel serverLevel = this.overworld();
        LOGGER.info("Preparing start region for dimension {}", serverLevel.dimension().location());
        BlockPos blockPos = serverLevel.getSharedSpawnPos();
        worldGenerationProgressListener.updateSpawnPos(new ChunkPos(blockPos));
        ServerChunkCache serverChunkCache = serverLevel.getChunkSource();
        serverChunkCache.getLightEngine().setTaskPerBatch(500);
        this.nextTickTime = Util.getMillis();

        overworld().getChunk(0, 0, ChunkStatus.FULL, true);
        overworld().getChunk(-5, -5, ChunkStatus.FULL, true);
        overworld().getChunk(-5, 5, ChunkStatus.FULL, true);
        overworld().getChunk(5, -5, ChunkStatus.FULL, true);
        overworld().getChunk(5, 5, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(0, 0, 0, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(2, 2, 2, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(-2, 2, 2, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(2, -2, 2, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(2, 2, -2, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(-2, -2, 2, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(2, -2, -2, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(-2, 2, -2, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(-2, -2, -2, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(5, 5, 5, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(-5, 5, 5, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(5, -5, 5, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(5, 5, -5, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(-5, -5, 5, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(5, -5, -5, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(-5, 5, -5, ChunkStatus.FULL, true);
        ((ICubicWorld) overworld()).getCube(-5, -5, -5, ChunkStatus.FULL, true);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            String s = ""; //Lets checkstyle pass
        }

        this.nextTickTime = Util.getMillis() + 10L;
        this.waitUntilNextTick();
        Iterator<ServerLevel> levelIter = this.levels.values().iterator();

        while (true) {
            ServerLevel serverLevel2;
            ForcedChunksSavedData forcedChunksSavedData;
            do {
                if (!levelIter.hasNext()) {
                    this.nextTickTime = Util.getMillis() + 10L;
                    this.waitUntilNextTick();
                    worldGenerationProgressListener.stop();
                    serverChunkCache.getLightEngine().setTaskPerBatch(5);
                    this.updateMobSpawningFlags();
                    return;
                }

                serverLevel2 = levelIter.next();
                forcedChunksSavedData = serverLevel2.getDataStorage().get(ForcedChunksSavedData::load, "chunks");
            } while (forcedChunksSavedData == null);

            LongIterator longIterator = forcedChunksSavedData.getChunks().iterator();

            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                ChunkPos chunkPos = new ChunkPos(l);
                serverLevel2.getChunkSource().updateChunkForced(chunkPos, true);
            }
        }
    }
}
