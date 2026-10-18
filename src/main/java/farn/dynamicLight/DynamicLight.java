package farn.dynamicLight;

import farn.dynamicLight.config.DynamicLightConfig;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.resource.DataResourceReloaderRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class DynamicLight {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    @EventListener
    public void registerReloader(DataResourceReloaderRegisterEvent event) {
        event.resourceManager.registerReloader(DynamicLightConfig.Reloader.INSTANCE);
    }
}
