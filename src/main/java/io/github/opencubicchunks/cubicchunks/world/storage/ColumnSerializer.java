package io.github.opencubicchunks.cubicchunks.world.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static net.minecraft.nbt.NbtIo.readCompressed;
import static net.minecraft.nbt.NbtIo.writeCompressed;

public class ColumnSerializer implements IChunkSerializer {
    public ChunkAccess read(ServerLevel level, StructureManager templateManager, PoiManager poiManager, ChunkPos chunkPos, ByteBuffer chunkBuffer) throws IOException {
        CompoundTag root = readCompressed(new ByteArrayInputStream(chunkBuffer.array()));
        if(root != null) {
            boolean flag = root.contains("Level", 10) && root.getCompound("Level").contains("Status", 8);
            if (flag) {
                return ChunkSerializer.read(level, templateManager, poiManager, chunkPos, root);
            }
        }
        return null;
    }

    public ByteBuffer write(ServerLevel level, ChunkAccess chunk, CompoundTag chunkNBT) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writeCompressed(chunkNBT, outputStream);
        return ByteBuffer.wrap(outputStream.toByteArray());
    }

    public ChunkStatus.ChunkType getChunkTypeFromBuffer(ByteBuffer chunkBuffer) throws IOException {
        return ChunkSerializer.getChunkTypeFromTag(readCompressed(new ByteArrayInputStream(chunkBuffer.array())));
    }
}
