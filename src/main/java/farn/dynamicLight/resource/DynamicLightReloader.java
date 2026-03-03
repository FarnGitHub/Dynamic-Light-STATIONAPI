package farn.dynamicLight.resource;

import farn.dynamicLight.config.DynamicLightLoader;
import net.modificationstation.stationapi.api.resource.Resource;
import net.modificationstation.stationapi.api.resource.ResourceManager;
import net.modificationstation.stationapi.api.resource.SinglePreparationResourceReloader;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.profiler.Profiler;

import java.util.Map;

public class DynamicLightReloader extends SinglePreparationResourceReloader<Map<Identifier, Resource>> {

    public static final DynamicLightReloader INSTANCE = new DynamicLightReloader();

    @Override
    public Map<Identifier, Resource> prepare(ResourceManager manager, Profiler profiler) {
        return manager.findResources("dynamic_light/item", (name) -> name.path.endsWith(".json"));
    }

    @Override
    public void apply(Map<Identifier, Resource> prepared, ResourceManager manager, Profiler profiler) {
        DynamicLightLoader.reload(prepared);
    }
}
