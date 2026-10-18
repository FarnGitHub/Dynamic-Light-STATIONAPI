package farn.dynamicLight.world.tick;

import farn.dynamicLight.config.data.LightData;
import farn.dynamicLight.config.data.LightDatas;
import farn.dynamicLight.world.light_source.LightSource;
import farn.dynamicLight.world.light_source.PlayerLightSource;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.*;

public enum WorldTick {
    INST;

    private int tick;
    private final List<LightSource> lightSources = new ArrayList<>();
    private final ObjectSet<Entity> toAdd = new ObjectOpenHashSet<>();
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
        for (Entity tempent : (Iterable<Entity>) world.entities) {
            if (tempent instanceof PlayerEntity || shouldEntityEmitLight(tempent)) {
                this.toAdd.add(tempent);
            } else if (tempent instanceof ItemEntity helpitem) {
                LightData data = LightDatas.INSTANCE.of(helpitem.stack.itemId);
                if (data != null && data.enabled) {
                    this.toAdd.add(tempent);
                }
            }
        }

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

        for(Entity entity : toAdd) { // now to loop the remainder of the fresh list, the NEW lights
            if(entity instanceof PlayerEntity player) {
                addLight(new PlayerLightSource(player));
            } else if(entity instanceof ItemEntity institem) {
                LightSource source = addLight(new LightSource(entity));
                LightData data = LightDatas.INSTANCE.of(institem.stack.itemId);
                if (data != null && data.enabled) {
                    source.setBrightness(data.brightness);
                    source.setWorkUnderWater(data.underwater);
                    source.setLit(true);
                }
            } else if(shouldEntityEmitLight(entity)) {
                LightSource source = addLight(new LightSource(entity));
                source.setBrightness(15);
                source.setLit(true);
            }
        }

        DynamicLightEngine.INST.computeLookup(lightSources);
    }

    private boolean shouldEntityEmitLight(Entity ent) {
        if(ent instanceof CreeperEntity creeperEntity)
            return creeperEntity.lastFuseTime > 0;
        return ent.isOnFire() || ent instanceof TntEntity;
    }

    private boolean shouldRemoveLight(Entity ent) {
        return !shouldEntityEmitLight(ent) || ent != null && !ent.isAlive();
    }

    private LightSource addLight(LightSource source) {
        lightSources.add(source);
        return source;
    }
}
