package farn.dynamicLight.other.cache;

public class LightCache
{
    final static int cacheSize = 0x8000;
	
    private LightCache()
    {
    }
    
    public static void clear()
    {
        for(int i=0; i<cacheSize; ++i)
            coords[i] = null;
    }

    private static int calcHash(int x, int y, int z) {
        final int m = 0x5bd1e995;
        final int r = 24;

        int h = 1234567890;

        h ^= mixHash(x, m, r);
        h ^= mixHash(y, m, r);
        h ^= mixHash(z, m, r);

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

    private static int mixHash(int k, int m, int r) {
        int h = k * m;
        h ^= h >>> r;
        h *= m;
        return h;
    }
    
    private static int findEntry(int x, int y, int z)
    {
        int i = Math.abs(calcHash(x, y, z))%cacheSize;
        int h = i;
        int j = 0;
        
        while(coords[i] != null && !coords[i].isEqual(x, y, z))
        {
            i = (i+1)%cacheSize;
            if(j++>cacheSize)
            {
                clear();
                return 0;
            }
        }
        
        return i;
    }

    public static float getLightValue(int x, int y, int z)
    {
        int i = findEntry(x, y, z);
        if(coords[i] == null)
        {
            return -1;
        }

        return lightValues[i];
    }

    public static void setLightValue(int x, int y, int z, float l)
    {
        int i = findEntry(x, y, z);
        coords[i] = BlockPosCache.getFromPool(x, y, z);
        lightValues[i] = l;
    }
    
    static BlockPosCache coords[];
    static float lightValues[];

    static {
        coords = new BlockPosCache[cacheSize];
        lightValues = new float[cacheSize];
    }
}