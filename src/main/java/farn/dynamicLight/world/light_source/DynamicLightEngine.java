package farn.dynamicLight.world.light_source;

import farn.dynamicLight.config.ItemLightInfo;
import farn.dynamicLight.config.ItemLightInfoLoader;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class DynamicLightEngine
{
	public static final int MAX_LIGHT_SOURCE = 128;
	private static LightSource[] lightSourceLookup = new LightSource[MAX_LIGHT_SOURCE];
	private static int lightSourcesSize = 0;

	public static ItemLightInfo of(int id) {
		return ItemLightInfoLoader.id2info.get(id);
	}
	
	public static int getBrightness(int x, int y, int z, int maxLight)
	{
		if(lightSourcesSize == 0 || maxLight == 15) return maxLight;

		int torchLight = 0;
		
		int lightBuffer;

		for(int l = hashAt(x, y, z); l < lightSourcesSize; ++l) {
			lightBuffer = lightSourceLookup[l].getLight(x, y, z);
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
		Arrays.sort(lightSourceLookup, 0, lightSourcesSize - 1, DynamicLightEngine::compareHash);
	}

	private static int compareHash(LightSource l1, LightSource l2) {
		return Integer.compare(hashAt(l1), hashAt(l2));
	}
}
