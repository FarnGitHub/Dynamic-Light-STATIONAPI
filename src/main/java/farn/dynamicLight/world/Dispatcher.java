package farn.dynamicLight.world;

import farn.dynamicLight.cache.ItemLightData;
import farn.dynamicLight.cache.BlockPosCache;
import farn.dynamicLight.cache.LightCache;
import farn.dynamicLight.config.DynamicLightLoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Dispatcher
{
	public static final List<LightSource> lightSources = new ArrayList<>();
	public static List<Entity> entitys = new ArrayList<>();
	public static Queue<Runnable> tickScheduler = new ConcurrentLinkedQueue<>();

	public static int getBrightness(int ID)
	{
		ItemLightData data = DynamicLightLoader.lightdataMap.get(ID);
		return data != null && data.enabled ? data.brightness : 0;
	}
	
	public static int getRange(int ID)
	{
		ItemLightData data = DynamicLightLoader.lightdataMap.get(ID);
		return data != null && data.enabled ? data.range : 0;
	}
	
	public static int getTimer(int ID)
	{
		ItemLightData data = DynamicLightLoader.lightdataMap.get(ID);
		return data != null && data.enabled ? data.timer : -1;
	}
	
	public static boolean workUnderWater(int ID)
	{
		ItemLightData data = DynamicLightLoader.lightdataMap.get(ID);
		return data != null && data.underwater;
	}
	
	public static float getBrightness(int i, int j, int k)
	{	
		float torchLight = 0.0F;
		
		float lightBuffer;
		
		for(LightSource torchLoopClass : lightSources)
        {
			lightBuffer = torchLoopClass.getLight(i, j, k);
			if(lightBuffer > torchLight)
			{
				torchLight = lightBuffer;
			}
		}
		
		return torchLight;
	}

	
	public static void addLight(LightSource playertorch)
    {
		tickScheduler.add(() -> {
			lightSources.add(playertorch);
			entitys.add(playertorch.getEntity());
		});
    }
	
	public static void removeLight(World world, LightSource playertorch)
	{
		tickScheduler.add(() -> {
			playertorch.setState(world, false);
			lightSources.remove(playertorch);
			entitys.remove(playertorch.getEntity());
		});
	}

	public static void clearCache() {
		LightCache.clear();
		BlockPosCache.clear();
	}
}
