package farn.dynamicLight.config.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Collection;

public final class LightDatas {

    private final Int2ObjectMap<LightData> lightMap = new Int2ObjectOpenHashMap<>();
    public static final LightDatas INSTANCE = new LightDatas();

    public LightData of(int id) {
        return lightMap.get(id);
    }

    public void add(int id, LightData info) {
        lightMap.put(id, info);
    }

    public void clearAll() {
        lightMap.clear();
    }

    public Collection<LightData> valuesView() {
        return lightMap.values();
    }

    private LightDatas() {
    }
}
