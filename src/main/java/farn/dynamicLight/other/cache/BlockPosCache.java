package farn.dynamicLight.other.cache;

import java.util.ArrayList;
import java.util.List;


public final class BlockPosCache
{

    public BlockPosCache(int i, int j, int k)
    {
        x = i;
        y = j;
        z = k;
    }
    
    public static BlockPosCache getFromPool(int i, int j, int k)
    {
        if(numBlockCoordsInUse >= blockCoords.size())
        {
            blockCoords.add(new BlockPosCache(i, j, k));
        }
        return (blockCoords.get(numBlockCoordsInUse++)).set(i, j, k);
    }
    
    public static void resetPool()
    {
        numBlockCoordsInUse = 0;
    }
    
    public BlockPosCache set(int i, int j, int k)
    {
        x = i;
        y = j;
        z = k;
        return this;
    }
    
    public boolean isEqual(int i, int j, int k)
    {
        return x == i && y == j && z == k;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof BlockPosCache otherCoord)
        {
            return x == otherCoord.x && y == otherCoord.y && z == otherCoord.z;
        } else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return (x << 16) ^ z ^(y<<24);
    }

    public int x;
    public int y;
    public int z;
    
    private static final List<BlockPosCache> blockCoords = new ArrayList<>();
    public static int numBlockCoordsInUse = 0;
}
