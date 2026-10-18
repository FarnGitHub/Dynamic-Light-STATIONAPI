package farn.dynamicLight.world.tick;

import farn.dynamicLight.world.light_source.LightSource;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.*;

public enum WorldTick {
    INST;

    private int tick;
    private final Int2ObjectMap<LightSource> id2lightSources = new Int2ObjectOpenHashMap<>(128);
    private World world;

    public void setWorld(World world) {
        this.world = world;
        this.id2lightSources.clear();
        DynamicLightEngine.INST.resetLookups();
    }

    public void tick() {
        if (world == null || ++this.tick % 5 != 0) return;

        //noinspection unchecked
        for (Entity entity : (Iterable<Entity>) world.entities)
            if(!id2lightSources.containsKey(entity.id) && entity.dynamiclight_shouldAdd())
                addLight(entity.dynamiclight_createLightSource());

        Collection<LightSource> lightSources = this.id2lightSources.values();

        Iterator<LightSource> iterator = lightSources.iterator();
        while(iterator.hasNext()) {
            LightSource source = iterator.next();
            Entity entity = source.getEntity();

            if (shouldRemoveLight(entity)) {
                source.setLit(false);
                iterator.remove();
            } else
                source.tick();
        }

        DynamicLightEngine.INST.computeLookup(lightSources);
    }

    private boolean shouldRemoveLight(Entity ent) {
        return ent != null && (!ent.dynamiclight_shouldAdd() || !ent.isAlive());
    }

    private void addLight(LightSource source) {
        id2lightSources.put(source.getEntity().id, source);
    }
}
