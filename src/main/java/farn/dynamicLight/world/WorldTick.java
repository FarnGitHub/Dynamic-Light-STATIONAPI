package farn.dynamicLight.world;

import net.minecraft.client.Minecraft;
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

    private static World curWorld;

    private WorldTick() {
    }

    public static void tick(Minecraft mc)
    {
        if (System.currentTimeMillis() >= prevTime + 50L)
        {
            tick(mc.world);
            tickEntity(mc);
            prevTime = System.currentTimeMillis();
        }
    }

    private static void tickEntity(Minecraft mc)
    {
        for(LightSource torchLoopClass : lightSources) {
            Entity torchent = torchLoopClass.getEntity();

            if(torchent instanceof PlayerEntity entPlayer) {
                tickPlayer(torchLoopClass, entPlayer);
            } else if(torchent instanceof ItemEntity itemEntity) {
                tickItemEntity(mc, torchLoopClass, itemEntity);
            } else {
                torchLoopClass.setPos(mc.world, torchent.x, torchent.y, torchent.z);
            }
        }
    }

    private static void tickPlayer(LightSource torchLoopClass, PlayerEntity entPlayer)
    {

        int itembrightness;
        if (entPlayer.getHand() != null)
        {
            int ID = entPlayer.getHand().itemId;
            if (ID != torchLoopClass.currentItemID)
            {
                torchLoopClass.currentItemID = ID;
                itembrightness = Dispatcher.getBrightness(ID);
                if (itembrightness > 0)
                {

                    torchLoopClass.setBrightness(itembrightness);
                    torchLoopClass.setRange(Dispatcher.getRange(ID));
                    torchLoopClass.setWorkUnderWater(Dispatcher.workUnderWater(ID));
                    torchLoopClass.setState(entPlayer.world, true, true);
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

    private static void tickItemEntity(Minecraft mc, LightSource torchLoopClass, Entity torchent)
    {
        torchLoopClass.setPos(mc.world, torchent.x, torchent.y, torchent.z);

        if (torchLoopClass.hasNoTimer()) {
            if (torchLoopClass.isDead()) {
                torchent.markDead();
                removeLight(mc.world, torchLoopClass);
            } else {
                torchLoopClass.timerTick();
            }
        }
    }

    private static void tick(World world)
    {
        if(world != curWorld) {
            curWorld = world;
            clearLightSources();
        }

        List<Entity> tempList = new ArrayList<>();

        //noinspection unchecked
        for (Entity tempent : (Iterable<Entity>) world.entities) {
            if (tempent instanceof PlayerEntity || shouldEntityEmitLight(tempent)) {
                tempList.add(tempent);
            } else if (tempent instanceof ItemEntity helpitem) {
                int brightness = Dispatcher.getBrightness(helpitem.stack.itemId);
                if (brightness > 0) {
                    tempList.add(tempent);
                }
            }
        }
        // tempList is now a fresh list of all Entities that can have a PlayerTorch
        Iterator<LightSource> itlightSources = lightSources.iterator();
        while(itlightSources.hasNext()) { // loop the old PlayerTorch List
            LightSource torchLoopClass = itlightSources.next();
            Entity torchent = torchLoopClass.getEntity();

            if (tempList.contains(torchent)) // check if the old entities are still in the world
            {
                tempList.remove(torchent); // if so remove them from the fresh list
            }
            else if ((!shouldEntityEmitLight(torchent)) // exclude foreign modded torches and burning stuff
                    || torchent != null && !torchent.isAlive()) // but do delete dead stuff
            {
                removeLight(world, torchLoopClass, itlightSources); // else remove them from the PlayerTorch list
            }
        }

        for(Entity newent : tempList) // now to loop the remainder of the fresh list, the NEW lights
        {

            LightSource newtorch = new LightSource(newent);
            addLight(newtorch);

            if(newent instanceof ItemEntity institem)
            {
                newtorch.setBrightness(Dispatcher.getBrightness(institem.stack.itemId));
                newtorch.setRange(Dispatcher.getRange(institem.stack.itemId));
                newtorch.setTimer(Dispatcher.getTimer(institem.stack.itemId));
                newtorch.setWorkUnderWater(Dispatcher.workUnderWater(institem.stack.itemId));
                newtorch.setState(world, true);
            } else if(shouldEntityEmitLight(newent) && !(newent instanceof PlayerEntity)) {
                newtorch.setBrightness(15);
                newtorch.setRange(31);
                newtorch.setState(world, true);
            }
        }

        Dispatcher.computeLightSource(lightSources);
    }

    private static boolean shouldEntityEmitLight(Entity ent) {
        if(ent instanceof CreeperEntity creeperEntity)
            return creeperEntity.lastFuseTime > 0;
        return ent.isOnFire() || ent instanceof TntEntity;
    }

    public static void addLight(LightSource playertorch)
    {
        if(lightSources.size() > Dispatcher.MAX_LIGHT_SOURCE)
            lightSources.remove(0);

        lightSources.add(playertorch);
    }

    public static void removeLight(World world, LightSource playertorch)
    {
        playertorch.setState(world, false);
        lightSources.remove(playertorch);
    }

    public static void removeLight(World world, LightSource playertorch, Iterator<LightSource> iterator)
    {
        playertorch.setState(world, false);
        iterator.remove();
    }

    public static void clearLightSources() {
        lightSources.clear();
    }
}
