package farn.dynamicLight.world;

import farn.dynamicLight.cache.ItemLightData;
import farn.dynamicLight.config.DynamicLightLoader;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class Dispatcher
{
	public static final int MAX_LIGHT_SOURCE = 128;
	private static LightSource[] lightSourceLookup = new LightSource[MAX_LIGHT_SOURCE];
	static int lightSourcesSize = 0;

	public static ItemLightData of(int id) {
		return DynamicLightLoader.lightdataMap.get(id);
	}
	
	public static int getBrightness(int i, int j, int k)
	{
		if(lightSourcesSize == 0) return 0;

		int torchLight = 0;
		
		int lightBuffer;

		for(int l = hashAt(i, j, k); l < lightSourcesSize; ++l) {
			lightBuffer = lightSourceLookup[l].getLight(i, j, k);
			if(lightBuffer > torchLight)
			{
				torchLight = lightBuffer;
			}
		}
		
		return MathHelper.floor(torchLight);
	}

	public static int hashCell(int cellX, int cellY, int cellZ) {
		return Math.abs(((cellX + 31) * 19 + cellY) * 41 + cellZ) * 83 & (lightSourcesSize - 1);
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

	public static void computeLightSource(Collection<LightSource> lightSources) {
		if(lightSources.isEmpty()) return;

		Arrays.fill(lightSourceLookup, null);
		lightSourceLookup = lightSources.toArray(lightSourceLookup);
		lightSourcesSize = lightSources.size();
		Arrays.sort(lightSourceLookup, 0, lightSourcesSize - 1, Dispatcher::compareHash);
	}

	private static int compareHash(LightSource l1, LightSource l2) {
		return Integer.compare(hashAt(l1), hashAt(l2));
	}
}
