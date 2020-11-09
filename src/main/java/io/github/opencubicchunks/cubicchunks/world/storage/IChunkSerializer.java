package io.github.opencubicchunks.cubicchunks.world.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface IChunkSerializer {

    ChunkAccess read(ServerLevel level, StructureManager templateManager, PoiManager poiManager, ChunkPos chunkPos, ByteBuffer chunkBuffer) throws IOException;

    ByteBuffer write(ServerLevel level, ChunkAccess chunk, CompoundTag chunkNBT) throws IOException;

    ChunkStatus.ChunkType getChunkTypeFromBuffer(ByteBuffer chunkBuffer) throws IOException;

}
