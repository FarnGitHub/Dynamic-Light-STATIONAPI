package farn.dynamicLight.other.world;

import farn.dynamicLight.other.mixin.CreeperEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.*;

public class WorldTick {

    static long prevTime;

    private WorldTick() {
    }

    public static void OnTickInGame(Minecraft mc)
    {
        if (System.currentTimeMillis() >= prevTime + 50L)
        {
            updateEntity(mc.world);
            tickEntity(mc);
            prevTime = System.currentTimeMillis();
        }
    }

    private static void tickEntity(Minecraft mc)
    {
        for(int j = 0; j < Dispatcher.itemArray.size(); j++) // loop the PlayerTorch List
        {
            LightSource torchLoopClass = (LightSource) Dispatcher.itemArray.get(j);
            Entity torchent = torchLoopClass.GetTorchEntity();

            if(torchent instanceof PlayerEntity entPlayer) {
                TickPlayerEntity(mc, torchLoopClass, entPlayer);
            } else if(torchent instanceof ItemEntity itemEntity) {
                TickItemEntity(mc, torchLoopClass, itemEntity);
            } else {
                torchLoopClass.setTorchPos(mc.world, (float)torchent.x, (float)torchent.y, (float)torchent.z);
            }
        }
    }

    private static void TickPlayerEntity(Minecraft mc, LightSource torchLoopClass, PlayerEntity entPlayer)
    {
        int oldbrightness = torchLoopClass.isTorchActive() ? torchLoopClass.GetTorchBrightness() : 0;

        if (GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0 && !entPlayer.isOnFire()) // case no (more) shiny armor
        {
            torchLoopClass.canArmorLit = false;
        }

        int itembrightness = 0;
        if (entPlayer.inventory.main[entPlayer.inventory.selectedSlot] != null)
        {
            int ID = entPlayer.inventory.main[entPlayer.inventory.selectedSlot].itemId;
            if (ID != torchLoopClass.currentItemID
                    || (!torchLoopClass.canArmorLit))
            {
                torchLoopClass.currentItemID = ID;

                itembrightness = Dispatcher.GetItemBrightnessValue(ID);
                if (itembrightness >= oldbrightness)
                {
                    if (torchLoopClass.canArmorLit)
                        torchLoopClass.canArmorLit = false;

                    torchLoopClass.SetTorchBrightness(itembrightness);
                    torchLoopClass.SetTorchRange(Dispatcher.GetItemLightRangeValue(ID));
                    torchLoopClass.SetWorksUnderwater(Dispatcher.GetItemWorksUnderWaterValue(ID));
                    torchLoopClass.setTorchState(entPlayer.world, true);
                }
                else if(!torchLoopClass.canArmorLit && GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0)
                {
                    torchLoopClass.setTorchState(entPlayer.world, false);
                }
            }
        }
        else
        {
            torchLoopClass.currentItemID = 0;
            if (!torchLoopClass.canArmorLit && GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0)
            {
                torchLoopClass.setTorchState(entPlayer.world, false);
            }
        }

        if (torchLoopClass.isTorchActive())
        {
            torchLoopClass.setTorchPos(entPlayer.world, (float)entPlayer.x, (float)entPlayer.y, (float)entPlayer.z);
        }
    }

    private static int GetPlayerArmorLightValue(LightSource torchLoopClass, PlayerEntity entPlayer, int oldbrightness)
    {
        int armorbrightness = 0;
        int armorID;


        if(entPlayer.isOnFire())
        {
            torchLoopClass.canArmorLit = true;
            torchLoopClass.SetTorchBrightness(15);
            torchLoopClass.SetTorchRange(31);
            torchLoopClass.setTorchState(entPlayer.world, true);
        }
        else
        {
            for(int l = 0; l < 4; l++)
            {
                ItemStack armorItem = entPlayer.inventory.getArmorStack(l);
                if(armorItem != null)
                {
                    armorID = armorItem.itemId;
                    armorbrightness = Dispatcher.GetItemBrightnessValue(armorID);

                    if (armorbrightness > oldbrightness)
                    {
                        oldbrightness = armorbrightness;
                        torchLoopClass.canArmorLit = true;
                        torchLoopClass.SetTorchBrightness(armorbrightness);
                        torchLoopClass.SetTorchRange(Dispatcher.GetItemLightRangeValue(armorID));
                        torchLoopClass.SetWorksUnderwater(Dispatcher.GetItemWorksUnderWaterValue(armorID));
                        torchLoopClass.setTorchState(entPlayer.world, true);
                    }
                }
            }
        }

        return armorbrightness;
    }

    private static void TickItemEntity(Minecraft mc, LightSource torchLoopClass, Entity torchent)
    {
        torchLoopClass.setTorchPos(mc.world, (float)torchent.x, (float)torchent.y, (float)torchent.z);

        if (torchLoopClass.hasDeathAge())
        {
            if (torchLoopClass.hasReachedDeathAge())
            {
                torchent.markDead();
                Dispatcher.RemoveTorch(mc.world, torchLoopClass);
            }
            else
            {
                torchLoopClass.doAgeTick();
            }
        }
    }

    private static void updateEntity(World worldObj)
    {
        List tempList = new ArrayList();

        for(int k = 0; k < worldObj.entities.size(); k++)
        {
            Entity tempent = (Entity)worldObj.entities.get(k);

            if(tempent instanceof PlayerEntity || shouldEmitLight(tempent)) {
                tempList.add(tempent);
            }
            else if(tempent instanceof ItemEntity)
            {
                ItemEntity helpitem = (ItemEntity)tempent;
                int brightness = Dispatcher.GetItemBrightnessValue(helpitem.stack.itemId);
                if (brightness > 0)
                {
                    tempList.add(tempent);
                }
            }

        }
        // tempList is now a fresh list of all Entities that can have a PlayerTorch

        for(int j = 0; j < Dispatcher.itemArray.size(); j++) // loop the old PlayerTorch List
        {
            LightSource torchLoopClass = (LightSource) Dispatcher.itemArray.get(j);
            Entity torchent = torchLoopClass.GetTorchEntity();

            if (tempList.contains(torchent)) // check if the old entities are still in the world
            {
                tempList.remove(torchent); // if so remove them from the fresh list
            }
            else if ((!shouldEmitLight(torchent)) // exclude foreign modded torches and burning stuff
                    || torchent != null && !torchent.isAlive()) // but do delete dead stuff
            {
                Dispatcher.RemoveTorch(worldObj, torchLoopClass); // else remove them from the PlayerTorch list
            }
        }

        for(int l = 0; l < tempList.size(); l++) // now to loop the remainder of the fresh list, the NEW lights
        {
            Entity newent = (Entity)tempList.get(l);

            LightSource newtorch = new LightSource(newent);
            Dispatcher.AddEntity(newtorch);

            if(newent instanceof ItemEntity)
            {
                ItemEntity institem = (ItemEntity)newent;
                newtorch.SetTorchBrightness(Dispatcher.GetItemBrightnessValue(institem.stack.itemId));
                newtorch.SetTorchRange(Dispatcher.GetItemLightRangeValue(institem.stack.itemId));
                newtorch.setDeathAge(Dispatcher.GetItemDeathAgeValue(institem.stack.itemId));
                newtorch.SetWorksUnderwater(Dispatcher.GetItemWorksUnderWaterValue(institem.stack.itemId));
                newtorch.setTorchState(worldObj, true);
            } else if(shouldEmitLight(newent) && !(newent instanceof PlayerEntity)) {
                newtorch.SetTorchBrightness(15);
                newtorch.SetTorchRange(31);
                newtorch.setTorchState(worldObj, true);
            }
        }
    }

    private static boolean shouldEmitLight(Entity ent) {
        if(ent instanceof CreeperEntity) {
            return ((CreeperEntityAccessor)ent).ignitedTime() > 0;
        }
        return ent.isOnFire() || ent instanceof TntEntity;
    }
}
