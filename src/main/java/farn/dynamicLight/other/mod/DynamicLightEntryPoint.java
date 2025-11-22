package farn.dynamicLight.other.mod;

import farn.dynamicLight.other.cache.ItemLightData;

import java.util.Map;

public interface DynamicLightEntryPoint {

    void onDefaultConfig(Map<Integer, ItemLightData> data);
}
