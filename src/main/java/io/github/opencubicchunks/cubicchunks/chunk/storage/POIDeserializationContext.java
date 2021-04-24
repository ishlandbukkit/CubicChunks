package io.github.opencubicchunks.cubicchunks.chunk.storage;

import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunkSection;

public interface POIDeserializationContext {

    void checkConsistencyWithBlocksForCube(SectionPos chunkPos, LevelChunkSection levelChunkSection);
}
