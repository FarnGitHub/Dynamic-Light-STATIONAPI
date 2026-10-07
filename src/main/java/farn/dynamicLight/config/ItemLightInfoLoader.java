package farn.dynamicLight.config;

import com.google.gson.Gson;
import farn.dynamicLight.DynamicLight;
import farn.dynamicLight.world.tick.WorldTick;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.modificationstation.stationapi.api.resource.Resource;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class ItemLightInfoLoader {

    public static boolean reloading = false;

    public static final Properties prop = new Properties();
    public static final File cfgFile = new File(FabricLoader.getInstance().getConfigDir().toString(), "dynamic_light_enables.cfg");
    public static Int2ObjectMap<ItemLightInfo> id2info = new Int2ObjectOpenHashMap<>();

    public static ItemLightInfo parseFromJson(Resource resource) throws IOException {
         return new Gson().fromJson(resource.getReader(), ItemLightInfo.class);
    }

    public static void reload(Map<Identifier, Resource> prepared) {
        reloading = true;
        id2info.clear();
        clearCache();
        for(Map.Entry<Identifier, Resource> entry : prepared.entrySet()) {
            ItemLightInfo data = null;
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
            id2info.put(data.getItem().id, data);
        }
        readConfig(false);
        reloading = false;
    }

    public static void clearCache() {
        WorldTick.clearLightSources();
    }

    public static void readConfig(boolean forceWrite) {
        if(!cfgFile.exists() || forceWrite){
            writeConfig();
        }

        try (FileInputStream in = new FileInputStream(cfgFile)) {
            prop.load(in);
            for(ItemLightInfo info : id2info.values()) {
                info.enabled = prop.getProperty(info.identifier, "true").equals("true");
            }
        } catch (IOException ignored) {
        }
    }

    public static void readConfig() {
        reloading = true;
        clearCache();
        readConfig(true);
        reloading = false;
    }

    public static void writeConfig() {
        try (FileOutputStream out = new FileOutputStream(cfgFile)) {
            for(ItemLightInfo info : id2info.values()) {
                prop.setProperty(info.identifier, info.enabled + "");
            }
            prop.store(out, "Dynamic Light Config");
        } catch (IOException ignored) {
        }
    }
}
