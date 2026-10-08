package farn.dynamicLight.world.light_source;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import java.util.*;

public class DynamicLightEngine
{
	public static final int MAX_LIGHT_SOURCE = 1024;
	private static final LightSourceLookup[] lightSourceLookup = new LightSourceLookup[MAX_LIGHT_SOURCE];
	private static final int[] startIndices = new int[MAX_LIGHT_SOURCE];
	private static final Vec3i[] CELL_OFFSETS;

	public static int getBrightness(int x, int y, int z, int light)
	{
		int cellX = positionToCell(x);
		int cellY = positionToCell(y);
		int cellZ = positionToCell(z);
		for(Vec3i v : CELL_OFFSETS) {
			int hash = hashCell(cellX+v.x, cellY+v.y, cellZ+v.z);
			int start = startIndices[hash];
			for(int l = start; l < lightSourceLookup.length; ++l) {
				LightSourceLookup lookup = lightSourceLookup[l];
				if(lookup == null || lookup.cellKey != hash) break;

				int torchLight = lookup.source.getLight(x, y, z);
				if(torchLight > light)
				{
					light = torchLight;
				}
			}
		}
		
		return MathHelper.floor(light);
	}

	public static int hashCell(int cellX, int cellY, int cellZ) {
		return Math.abs(((cellX + 31) * 19 + cellY) * 41 + cellZ) * 83 & (lightSourceLookup.length - 1);
	}

	public static int hashAt(int x, int y, int z) {
		return hashCell(positionToCell(x), positionToCell(y), positionToCell(z));
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
		Arrays.fill(startIndices, 0);
		Iterator<LightSourceLookup> it =
				lightSources.
				stream().
				limit(MAX_LIGHT_SOURCE).
				map((l) -> new LightSourceLookup(hashAt(l), l)).
				sorted(Comparator.comparingInt(LightSourceLookup::cellKey)).
				iterator();

		int i = 0;
		while (it.hasNext()) {
			lightSourceLookup[i] = it.next();
			i++;
		}

		for (i = 0; i < lightSourceLookup.length; i++) {
			LightSourceLookup entry = lightSourceLookup[i];

			if (entry == null) {
				break;
			}

			int key = entry.cellKey();
			int previousKey = i == 0 ? Integer.MAX_VALUE : Objects.requireNonNull(lightSourceLookup[i - 1]).cellKey();

			if (key != previousKey) {
				startIndices[key] = i;
			}
		}

	}

	static {
		CELL_OFFSETS = new Vec3i[27];
		int i = 0;

		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					CELL_OFFSETS[i] = new Vec3i(x, y, z);
					i++;
				}
			}
		}
	}

	private record LightSourceLookup(int cellKey, LightSource source) {
	}
}
