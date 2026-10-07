package farn.dynamicLight.world;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.World;

public class ChunkUpdater {
    private static final LongSet chunkNeedUpdate = new LongOpenHashSet();

    public static void markDirty(int minX, int minZ, int maxX, int maxZ) {
        int minXChunk = minX >> 4;
        int minZChunk = minZ >> 4;
        int maxXChunk = maxX >> 4;
        int maxZChunk = maxZ >> 4;
        for(int x = minXChunk; x <= maxXChunk; x++) {
            for(int z = minZChunk; z <= maxZChunk; z++) {
                long packedChunkPos = (((long)x) << 32) | (z & 0xffffffffL);
                if(!chunkNeedUpdate.contains(packedChunkPos)) {
                    chunkNeedUpdate.add(packedChunkPos);
                }
            }
        }

    }

    public static void updateAllDirty(World world) {
        LongIterator iterator = chunkNeedUpdate.iterator();
        while(iterator.hasNext()) {
            long l = iterator.nextLong();
            int x = (int)(l >> 32) * 16;
            int z = (int)l * 16;
            world.setBlocksDirty(x, z, world.getBottomY(), world.getTopY());
            iterator.remove();
        }
    }

    public static void clearAllDirty() {
        chunkNeedUpdate.clear();
    }
}
