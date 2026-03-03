package farn.dynamicLight.cache;

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
    
    public static BlockPosCache get(int i, int j, int k)
    {
        if(currentIndex >= caches.size())
        {
            caches.add(new BlockPosCache(i, j, k));
        }
        return (caches.get(currentIndex++)).set(i, j, k);
    }
    
    public static void clear()
    {
        currentIndex = 0;
        if(caches.size() == Integer.MAX_VALUE) caches.clear();
    }
    
    public BlockPosCache set(int i, int j, int k)
    {
        x = i;
        y = j;
        z = k;
        return this;
    }
    
    public boolean equals(int i, int j, int k)
    {
        return x == i && y == j && z == k;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof BlockPosCache other)
        {
            return x == other.x && y == other.y && z == other.z;
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
    
    private static final List<BlockPosCache> caches = new ArrayList<>();
    public static int currentIndex = 0;
}
