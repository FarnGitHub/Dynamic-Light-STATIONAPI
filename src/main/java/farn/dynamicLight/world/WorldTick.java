package farn.dynamicLight.world;

import farn.dynamicLight.cache.ItemLightData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.*;

public class WorldTick {

    private static long prevTime;

    private static final List<LightSource> lightSources = new ArrayList<>();
    private static boolean lightSourcesChanged = false;

    private static World curWorld;

    private WorldTick() {
    }

    public static void tick(World world)
    {
        if(world != curWorld) {
            curWorld = world;
            clearLightSources();
        }

        if (world != null && System.currentTimeMillis() >= prevTime + 50L)
        {
            collectEntity(world);
            tickEntity(world);
            ChunkUpdater.updateAllDirty(world);
            prevTime = System.currentTimeMillis();
        }
    }

    private static void tickEntity(World world)
    {
        for(LightSource torchLoopClass : lightSources) {
            Entity torchent = torchLoopClass.getEntity();

            if(torchent instanceof PlayerEntity entPlayer) {
                tickPlayer(torchLoopClass, entPlayer);
            } else if(torchent instanceof ItemEntity itemEntity) {
                tickItemEntity(torchLoopClass, itemEntity);
            } else {
                torchLoopClass.setPos(world, torchent.x, torchent.y, torchent.z);
            }
        }
    }

    private static void tickPlayer(LightSource torchLoopClass, PlayerEntity entPlayer)
    {

        if (entPlayer.getHand() != null)
        {
            int ID = entPlayer.getHand().itemId;
            if (ID != torchLoopClass.currentItemID)
            {
                torchLoopClass.currentItemID = ID;
                ItemLightData data = Dispatcher.of(ID);
                if (data != null && data.enabled) {
                    torchLoopClass.setBrightness(data.brightness);
                    torchLoopClass.setRange(data.range);
                    torchLoopClass.setWorkUnderWater(data.underwater);
                    torchLoopClass.setState(entPlayer.world, true, true);
                } else {
                    torchLoopClass.setState(entPlayer.world, false);
                }
            }
        }
        else
        {
            torchLoopClass.currentItemID = 0;
            torchLoopClass.setState(entPlayer.world, false);
        }

        if (torchLoopClass.active())
        {
            torchLoopClass.setPos(entPlayer.world, entPlayer.x, entPlayer.y, entPlayer.z);
        }
    }

    private static void tickItemEntity(LightSource torchLoopClass, Entity torchent)
    {
        torchLoopClass.setPos(torchent.world, torchent.x, torchent.y, torchent.z);

        if (!torchLoopClass.hasNoTimer()) {
            if (torchLoopClass.isDead()) {
                torchLoopClass.setState(torchent.world, false);
            } else {
                torchLoopClass.timerTick();
            }
        }
    }

    private static void collectEntity(World world) {
        List<Entity> tempList = new ArrayList<>();

        //noinspection unchecked
        for (Entity tempent : (Iterable<Entity>) world.entities) {
            if (tempent instanceof PlayerEntity || shouldEntityEmitLight(tempent)) {
                tempList.add(tempent);
            } else if (tempent instanceof ItemEntity helpitem) {
                ItemLightData data = Dispatcher.of(helpitem.stack.itemId);
                if (data != null && data.enabled) {
                    tempList.add(tempent);
                }
            }
        }
        // tempList is now a fresh list of all Entities that can have a PlayerTorch
        Iterator<LightSource> itlightSources = lightSources.iterator();
        while(itlightSources.hasNext()) { // loop the old PlayerTorch List
            LightSource torchLoopClass = itlightSources.next();
            Entity torchent = torchLoopClass.getEntity();

            if (tempList.contains(torchent)) { // check if the old entities are still in the world
                tempList.remove(torchent); // if so remove them from the fresh list
            } else if (shouldRemoveLight(torchent)) {// delete dead stuff
                removeLight(world, torchLoopClass, itlightSources); // else remove them from the PlayerTorch list
            }
        }

        for(Entity newent : tempList) // now to loop the remainder of the fresh list, the NEW lights
        {
            LightSource newtorch;
            if(newent instanceof ItemEntity institem)
            {
                ItemLightData data = Dispatcher.of(institem.stack.itemId);
                if(data != null && data.enabled) {
                    addLight(newtorch = new LightSource(newent));
                    newtorch.setBrightness(data.brightness);
                    newtorch.setRange(data.range);
                    newtorch.setTimer(data.timer);
                    newtorch.setWorkUnderWater(data.underwater);
                    newtorch.setState(world, true);
                }
            } else if(shouldEntityEmitLight(newent) && !(newent instanceof PlayerEntity)) {
                addLight(newtorch = new LightSource(newent));
                newtorch.setBrightness(15);
                newtorch.setRange(31);
                newtorch.setState(world, true);
            } else {
                addLight(new LightSource(newent));
            }
        }

        if(lightSourcesChanged)
            Dispatcher.computeLightSource(lightSources);
    }

    private static boolean shouldEntityEmitLight(Entity ent) {
        if(ent instanceof CreeperEntity creeperEntity)
            return creeperEntity.lastFuseTime > 0;
        return ent.isOnFire() || ent instanceof TntEntity;
    }

    private static boolean shouldRemoveLight(Entity ent) {
        return !shouldEntityEmitLight(ent) || ent != null && !ent.isAlive();
    }

    public static void addLight(LightSource playertorch)
    {
        if(lightSources.size() > Dispatcher.MAX_LIGHT_SOURCE)
            lightSources.remove(0);

        lightSources.add(playertorch);
        lightSourcesChanged = true;
    }

    public static void removeLight(World world, LightSource playertorch, Iterator<LightSource> iterator)
    {
        playertorch.setState(world, false);
        iterator.remove();
        lightSourcesChanged = true;
    }

    public static void clearLightSources() {
        lightSources.clear();
        ChunkUpdater.clearAllDirty();
        lightSourcesChanged = true;
    }
}
