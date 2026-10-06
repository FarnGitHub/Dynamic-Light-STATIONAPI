package farn.dynamicLight.world;

import farn.dynamicLight.cache.ItemLightData;
import farn.dynamicLight.config.DynamicLightLoader;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.*;

public class Dispatcher
{
	public static final List<LightSource> lightSources = new ArrayList<>();
	public static LightSource[] soredLightSources;

	public static int getBrightness(int ID)
	{
		ItemLightData data = of(ID);
		return data != null && data.enabled ? data.brightness : 0;
	}
	
	public static int getRange(int ID)
	{
		ItemLightData data = of(ID);
		return data != null && data.enabled ? data.range : 0;
	}
	
	public static int getTimer(int ID)
	{
		ItemLightData data = of(ID);
		return data != null && data.enabled ? data.timer : -1;
	}
	
	public static boolean workUnderWater(int ID)
	{
		ItemLightData data = of(ID);
		return data != null && data.enabled && data.underwater;
	}

	private static ItemLightData of(int id) {
		return DynamicLightLoader.lightdataMap.get(Item.ITEMS[id]);
	}
	
	public static float getBrightness(int i, int j, int k)
	{	
		float torchLight = 0.0F;
		
		float lightBuffer;

		int startIndex = hashAt(i, j, k) % soredLightSources.length;
		for(int l = startIndex; l < soredLightSources.length; ++l) {
			lightBuffer = lightSources.get(l).getLight(i, j, k);
			if(lightBuffer > torchLight)
			{
				torchLight = lightBuffer;
			}
		}
		
		return torchLight;
	}

	
	public static void addLight(LightSource playertorch)
    {
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

	public static int hashCell(int cellX, int cellY, int cellZ) {
		return Math.abs(((cellX + 31) * 19 + cellY) * 41 + cellZ) * 83 & (lightSources.size() - 1);
	}

	public static int hashAt(int x, int y, int z) {
		return hashCell(
				positionToCell(x),
				positionToCell(y),
				positionToCell(z)
		);
	}
	public static int hashAt(LightSource source) {
		return hashAt(source.iX, source.iY, source.iZ);
	}

	public static int positionToCell(int coordinate) {
		return coordinate >> 3;
	}

	public static void computedSortedLightSources() {
		soredLightSources = lightSources.stream().sorted(Dispatcher::compareByKey).toArray(LightSource[]::new);
	}

	private static int compareByKey(LightSource source1, LightSource source2) {
		return Integer.compare(hashAt(source1), hashAt(source2));
	}
}
