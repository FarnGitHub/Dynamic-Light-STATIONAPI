package farn.dynamicLight.world.tick;

import farn.dynamicLight.world.light_source.LightSource;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.*;

public enum WorldTick {
    INST;

    private int tick;
    private final List<LightSource> lightSources = new ArrayList<>(128);
    private final Set<Entity> toAdd = new ObjectOpenHashSet<>(128);
    private World world;

    public void setWorld(World world) {
        this.world = world;
        this.lightSources.clear();
        this.toAdd.clear();
        DynamicLightEngine.INST.resetLookups();
    }

    public void tick() {
        if (world == null || ++this.tick % 5 != 0) return;

        //noinspection unchecked
        for (Entity entity : (Iterable<Entity>) world.entities)
            if(entity.dynamiclight_shouldAddToLightSource())
                this.toAdd.add(entity);

        Iterator<LightSource> iterator = lightSources.iterator();
        while(iterator.hasNext()) {
            LightSource source = iterator.next();
            Entity entity = source.getEntity();

            if (this.toAdd.contains(entity)) {
                this.toAdd.remove(entity);
                source.tick();
            } else if (shouldRemoveLight(entity)) {
                source.setLit(false);
                iterator.remove();
            }
        }

        for (Entity entity : toAdd)
            addLight(entity.dynamiclight_createLightSource());

        toAdd.clear();
        DynamicLightEngine.INST.computeLookup(lightSources);
    }

    private boolean shouldRemoveLight(Entity ent) {
        return ent != null && (!ent.dynamiclight_shouldAddToLightSource() || !ent.isAlive());
    }

    private void addLight(LightSource source) {
        lightSources.add(source);
    }
}
