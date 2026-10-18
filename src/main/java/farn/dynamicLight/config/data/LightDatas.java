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

    public boolean isEnabled(LightData lightData) {
        return lightData != null && lightData.enabled;
    }

    public boolean isEnabled(int id) {
        return isEnabled(of(id));
    }

    public void add(int id, LightData info) {
        lightMap.put(id, info);
    }

    public void clearAll() {
        lightMap.clear();
    }

    public Collection<LightData> values() {
        return lightMap.values();
    }

    private LightDatas() {
    }
}
