package io.github.opencubicchunks.cubicchunks.world.storage;

import io.github.opencubicchunks.cubicchunks.chunk.IBigCube;
import io.github.opencubicchunks.cubicchunks.chunk.util.CubePos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ICubeSerializer {

    IBigCube read(ServerLevel level, StructureManager templateManager, PoiManager poiManager, CubePos cubePos, ByteBuffer cubeBuffer) throws IOException;

    ByteBuffer write(ServerLevel level, IBigCube cube, CompoundTag cubeNBT) throws IOException;

    ChunkStatus.ChunkType getChunkTypeFromBuffer(ByteBuffer buffer) throws IOException;

}