package farn.dynamicLight.other.world;

import farn.dynamicLight.other.config.Config;
import farn.dynamicLight.other.cache.ItemLightData;
import farn.dynamicLight.other.cache.BlockPosCache;
import farn.dynamicLight.other.cache.LightCache;
import net.minecraft.world.World;

import java.util.*;

public class Dispatcher
{
	public static java.util.List itemArray = new ArrayList();
	public static java.util.List entityArray = new ArrayList();
	public static Map<Integer, ItemLightData> lightdataMap = new HashMap<>();

	public static int GetItemBrightnessValue(int ID)
	{
		ItemLightData data = lightdataMap.get(ID);
		return data != null ? data.brightness : 0;
	}
	
	public static int GetItemLightRangeValue(int ID)
	{
		ItemLightData data = lightdataMap.get(ID);
		return data != null ? data.range : 0;
	}
	
	public static int GetItemDeathAgeValue(int ID)
	{
		ItemLightData data = lightdataMap.get(ID);
		return data != null ? data.deathAge : -1;
	}
	
	public static boolean GetItemWorksUnderWaterValue(int ID)
	{
		ItemLightData data = lightdataMap.get(ID);
		return data != null && data.underwater;
	}
	
	public static float getLightBrightness(net.minecraft.world.World world, int i, int j, int k)
	{	
		float torchLight = 0.0F;
		
		float lightBuffer;
		
		for(int x = 0; x < itemArray.size(); x++)
        {
			LightSource torchLoopClass = (LightSource) itemArray.get(x);
			lightBuffer = torchLoopClass.getTorchLight(world, i, j, k);
			if(lightBuffer > torchLight)
			{
				torchLight = lightBuffer;
			}
		}
		
		return torchLight;
	}

	
	public static void AddEntity(LightSource playertorch)
    {
		if(!Config.resetLight) {
			itemArray.add(playertorch);
			entityArray.add(playertorch.GetTorchEntity());
		}
    }
	
	public static void RemoveTorch(World world, LightSource playertorch)
	{
		if(!Config.resetLight) {
			playertorch.setTorchState(world, false);
			itemArray.remove(playertorch);
			entityArray.remove(playertorch.GetTorchEntity());
		}
	}

	public static void clearCacheAndResetPool() {
		LightCache.clear();
		BlockPosCache.resetPool();
	}
}
