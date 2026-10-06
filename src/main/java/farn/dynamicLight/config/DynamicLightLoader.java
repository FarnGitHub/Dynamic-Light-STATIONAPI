package farn.dynamicLight.config;

import com.google.gson.Gson;
import farn.dynamicLight.DynamicLight;
import farn.dynamicLight.cache.ItemLightData;
import farn.dynamicLight.world.Dispatcher;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.resource.Resource;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

public class DynamicLightLoader {

    public static boolean reloading = false;

    public static final Properties properties = new Properties();
    public static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toString(), "dynamic_light_enables.cfg");
    public static WeakHashMap<Item, ItemLightData> lightdataMap = new WeakHashMap<>();

    public static ItemLightData parseFromJson(Resource resource) throws IOException {
         return new Gson().fromJson(resource.getReader(), ItemLightData.class);
    }

    public static void reload(Map<Identifier, Resource> prepared) {
        reloading = true;
        lightdataMap.clear();
        clearAllCache();
        for(Map.Entry<Identifier, Resource> entry : prepared.entrySet()) {
            ItemLightData data = null;
            try {
                data = parseFromJson(entry.getValue());
            } catch (Exception e) {
                DynamicLight.LOGGER.error("Failed to parse {} : {}", entry.getKey(), e.getMessage());
            }
            if(data == null || data.getItem() == null) continue;
            data.enabled = true;
            try {
                data.itemNames = data.getItem().getTranslatedName();
            } catch (Exception e) {
                data.itemNames = data.identifier;
            }
            DynamicLight.LOGGER.info("Add Dynamic light from {}", entry.getKey());
            lightdataMap.put(data.getItem(), data);
        }
        readConfig(false);
        reloading = false;
    }

    public static void clearAllCache() {
        Dispatcher.lightSources.clear();
    }

    public static void readConfig(boolean forceWrite) {
        if(!configFile.exists() || forceWrite){
            writeConfig();
        }

        try (FileInputStream in = new FileInputStream(configFile)) {
            properties.load(in);
            for(Map.Entry<Item, ItemLightData> dataEntry : lightdataMap.entrySet()) {
                dataEntry.getValue().enabled = properties.getProperty(dataEntry.getValue().identifier, "true").equals("true");
            }
        } catch (IOException ignored) {
        }
    }

    public static void readConfig() {
        reloading = true;
        clearAllCache();
        readConfig(true);
        reloading = false;
    }

    public static void writeConfig() {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            for(Map.Entry<Item, ItemLightData> dataEntry : lightdataMap.entrySet()) {
                properties.setProperty(dataEntry.getValue().identifier, dataEntry.getValue().enabled + "");
            }
            properties.store(out, "Dynamic Light Config");
        } catch (IOException ignored) {
        }
    }
}
