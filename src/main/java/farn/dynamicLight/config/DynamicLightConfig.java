package farn.dynamicLight.config;

import com.google.gson.Gson;
import farn.dynamicLight.DynamicLight;
import farn.dynamicLight.config.data.LightData;
import farn.dynamicLight.config.data.LightDatas;
import net.fabricmc.loader.api.FabricLoader;
import net.modificationstation.stationapi.api.resource.DataManager;
import net.modificationstation.stationapi.api.resource.Resource;
import net.modificationstation.stationapi.api.resource.ResourceManager;
import net.modificationstation.stationapi.api.resource.SinglePreparationResourceReloader;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.profiler.Profiler;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public final class DynamicLightConfig {

    private final Properties PROP = new Properties();
    public final File CFG_FILE = new File(FabricLoader.getInstance().getConfigDir().toString(), "dynamic_light_enables.cfg");
    public static final DynamicLightConfig INSTANCE = new DynamicLightConfig();

    private DynamicLightConfig() {
    }

    public void read(boolean forceWrite) {
        if(!CFG_FILE.exists() || forceWrite)  write();

        try (FileInputStream in = new FileInputStream(CFG_FILE)) {
            PROP.load(in);
            for(LightData info : LightDatas.INSTANCE.values())
                info.enabled = PROP.getProperty(info.identifier, "true").equals("true");
        } catch (IOException ignored) {
        }
    }

    public void read() {
        read(true);
    }

    public void write() {
        try (FileOutputStream out = new FileOutputStream(CFG_FILE)) {
            for(LightData info : LightDatas.INSTANCE.values())
                PROP.setProperty(info.identifier, info.enabled + "");
            PROP.store(out, "Dynamic Light Config");
        } catch (IOException ignored) {
        }
    }

    public static class Reloader extends SinglePreparationResourceReloader<Map<Identifier, Resource>> {
        private final Gson GSON = new Gson();

        public static final Reloader INSTANCE = new Reloader();

        private Reloader() {
        }

        @Override
        protected Map<Identifier, Resource> prepare(ResourceManager manager, Profiler profiler) {
            return DataManager.INSTANCE.findResources(
                    "dynamic_light/item",
                    (name) -> name.path.endsWith(".json")
            );
        }

        @Override
        protected void apply(Map<Identifier, Resource> prepared, ResourceManager manager, Profiler profiler) {
            LightDatas.INSTANCE.clearAll();
            for(Map.Entry<Identifier, Resource> entry : prepared.entrySet()) {
                LightData data;
                try {
                    data = parseFromJson(entry.getValue());
                } catch (Exception e) {
                    DynamicLight.LOGGER.error("Failed to parse {} : {}", entry.getKey(), e.getMessage());
                    return;
                }
                if(data == null || data.getItem() == null) continue;
                DynamicLight.LOGGER.info("Add Dynamic light from {}", entry.getKey());
                LightDatas.INSTANCE.add(data.getItem().id, data);
            }
            DynamicLightConfig.INSTANCE.read(false);
        }

        private LightData parseFromJson(Resource resource) throws IOException {
            return GSON.fromJson(resource.getReader(), LightData.class);
        }
    }
}
