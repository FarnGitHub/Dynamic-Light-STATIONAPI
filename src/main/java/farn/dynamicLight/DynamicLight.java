package farn.dynamicLight;

import farn.dynamicLight.config.ItemLightInfoLoader;
import farn.dynamicLight.config.ItemLightInfoReloader;
import farn.dynamicLight.world.tick.WorldTick;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.client.Minecraft;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.event.resource.DataReloadEvent;
import net.modificationstation.stationapi.api.event.resource.DataResourceReloaderRegisterEvent;
import net.modificationstation.stationapi.api.event.tick.GameTickEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.resource.DataManager;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.api.util.profiler.DummyProfiler;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class DynamicLight {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    @EventListener
    public void initFinished(InitEvent e) {
        FabricLoader.getInstance().getEntrypointContainers("dynamic_light:before_init", Object.class).forEach(EntrypointManager::setup);
    }

    @EventListener
    public void registerReloader(DataResourceReloaderRegisterEvent e) {
        e.resourceManager.registerReloader(ItemLightInfoReloader.INSTANCE);
    }

    @EventListener(priority = ListenerPriority.LOWEST)
    public void dataEvent(DataReloadEvent event) {
        ItemLightInfoReloader.INSTANCE.apply(
                ItemLightInfoReloader.INSTANCE.prepare(
                        DataManager.INSTANCE,
                        DummyProfiler.INSTANCE),
                DataManager.INSTANCE,
                DummyProfiler.INSTANCE
        );
    }
}
