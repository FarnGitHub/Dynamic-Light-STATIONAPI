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

    static long prevTime;

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
        for(LightSource torchLoopClass : Dispatcher.lightSources) {
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
        int oldbrightness = torchLoopClass.active() ? torchLoopClass.getBrightness() : 0;

        int itembrightness;
        if (entPlayer.getHand() != null)
        {
            int ID = entPlayer.getHand().itemId;
            if (ID != torchLoopClass.currentItemID)
            {
                torchLoopClass.currentItemID = ID;

                itembrightness = Dispatcher.getBrightness(ID);
                if (itembrightness >= oldbrightness)
                {

                    torchLoopClass.setBrightness(itembrightness);
                    torchLoopClass.setRange(Dispatcher.getRange(ID));
                    torchLoopClass.setWorkUnderWater(Dispatcher.workUnderWater(ID));
                    torchLoopClass.setState(entPlayer.world, true);
                }
                else
                {
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

    private static void tickItemEntity(Minecraft mc, LightSource torchLoopClass, Entity torchent)
    {
        torchLoopClass.setPos(mc.world, torchent.x, torchent.y, torchent.z);

        if (torchLoopClass.hasNoTimer()) {
            if (torchLoopClass.isDead()) {
                torchent.markDead();
                Dispatcher.removeLight(mc.world, torchLoopClass);
            } else {
                torchLoopClass.timerTick();
            }
        }
    }

    private static void tick(World worldObj)
    {
        List<Entity> tempList = new ArrayList<>();

        //noinspection unchecked
        for (Entity tempent : (Iterable<Entity>) worldObj.entities) {
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
        Iterator<LightSource> lightSources = Dispatcher.lightSources.iterator();
        while(lightSources.hasNext()) { // loop the old PlayerTorch List
            LightSource torchLoopClass = lightSources.next();
            Entity torchent = torchLoopClass.getEntity();

            if (tempList.contains(torchent)) // check if the old entities are still in the world
            {
                tempList.remove(torchent); // if so remove them from the fresh list
            }
            else if ((!shouldEntityEmitLight(torchent)) // exclude foreign modded torches and burning stuff
                    || torchent != null && !torchent.isAlive()) // but do delete dead stuff
            {
                Dispatcher.removeLight(worldObj, torchLoopClass, lightSources); // else remove them from the PlayerTorch list
            }
        }

        for(Entity newent : tempList) // now to loop the remainder of the fresh list, the NEW lights
        {

            LightSource newtorch = new LightSource(newent);
            Dispatcher.addLight(newtorch);

            if(newent instanceof ItemEntity institem)
            {
                newtorch.setBrightness(Dispatcher.getBrightness(institem.stack.itemId));
                newtorch.setRange(Dispatcher.getRange(institem.stack.itemId));
                newtorch.setTimer(Dispatcher.getTimer(institem.stack.itemId));
                newtorch.setWorkUnderWater(Dispatcher.workUnderWater(institem.stack.itemId));
                newtorch.setState(worldObj, true);
            } else if(shouldEntityEmitLight(newent) && !(newent instanceof PlayerEntity)) {
                newtorch.setBrightness(15);
                newtorch.setRange(31);
                newtorch.setState(worldObj, true);
            }
        }

        Dispatcher.computedSortedLightSources();
    }

    private static boolean shouldEntityEmitLight(Entity ent) {
        if(ent instanceof CreeperEntity creeperEntity)
            return creeperEntity.lastFuseTime > 0;
        return ent.isOnFire() || ent instanceof TntEntity;
    }
}
