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

public class DynamicLightLoader {

    public static boolean reloading = false;

    public static final Properties properties = new Properties();
    public static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toString(), "dynamic_light.prop");

    public static ItemLightData parseFromJson(Resource resource) throws IOException {
         return new Gson().fromJson(resource.getReader(), ItemLightData.class);
    }

    public static void reload(Map<Identifier, Resource> prepared) {
        reloading = true;
        Dispatcher.lightdataMap.clear();
        clearAllCache();
        prepared.forEach((iden, resource) -> {
            ItemLightData data = null;
            try {
                data = parseFromJson(resource);
            } catch (Exception e) {
                DynamicLight.LOGGER.error("Failed to parse {} : {}", iden, e.getMessage());
            }
            if(data == null) return;
            data.enabled = true;
            try {
                data.itemNames = Item.ITEMS[data.getItemId()].getTranslatedName();
            } catch (Exception e) {
                data.itemNames = data.identifier;
            }
            DynamicLight.LOGGER.info("Add Dynamic light from {}", iden);
            Dispatcher.lightdataMap.put(data.getItemId(), data);
        });
        readConfig(false);
        reloading = false;
    }

    public static void clearAllCache() {
        Dispatcher.clearCache();
        Dispatcher.lightSources.clear();
        Dispatcher.entitys.clear();
    }

    public static void readConfig(boolean forceWrite) {
        if(!configFile.exists() || forceWrite){
            writeConfig();
        }

        try (FileInputStream in = new FileInputStream(configFile)) {
            properties.load(in);
            for(Int2ObjectMap.Entry<ItemLightData> dataEntry: Dispatcher.lightdataMap.int2ObjectEntrySet()) {
                dataEntry.getValue().enabled = properties.getProperty(dataEntry.getValue().identifier, "true").equals("true");
            }
        } catch (IOException e) {
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
            for(Int2ObjectMap.Entry<ItemLightData> dataEntry: Dispatcher.lightdataMap.int2ObjectEntrySet()) {
                properties.setProperty(dataEntry.getValue().identifier, dataEntry.getValue().enabled + "");
            }
            properties.store(out, "Dynamic Light Config");
        } catch (IOException e) {
        }
    }
}
