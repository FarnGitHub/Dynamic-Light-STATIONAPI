package farn.dynamicLight.world.light_source;

import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DynamicLightEngine
{
	public static final int DEFAULT_SIZE = 128;

	private static final Vec3i[] CELL_OFFSETS;
	private static LookupEntry[] lookups = new LookupEntry[DEFAULT_SIZE];
	private static int[] startIndices = new int[DEFAULT_SIZE];

	public static int getBrightness(int x, int y, int z, int light)
	{
		int cX = positionToCell(x);
		int cY = positionToCell(y);
		int cZ = positionToCell(z);
		for(Vec3i v : CELL_OFFSETS) {
			int hash = hashCell(cX+v.x, cY+v.y, cZ+v.z);
			int start = startIndices[hash];
			for(int l = start; l < lookups.length; ++l) {
				LookupEntry lookup = lookups[l];
				if(lookup == null || lookup.cellKey != hash) break;

				int torchLight = lookup.source.getLight(x, y, z);
				if(torchLight > light) light = torchLight;
			}
		}

		return Math.min(Math.max(light, 0), 15);
	}

	private static int hashCell(int cellX, int cellY, int cellZ) {
		return Math.abs(((cellX + 31) * 19 + cellY) * 41 + cellZ) * 83 & (lookups.length - 1);
	}

	private static int hashAt(int x, int y, int z) {
		return hashCell(positionToCell(x), positionToCell(y), positionToCell(z));
	}
	private static int hashAt(LightSource source) {
		return hashAt(source.iX, source.iY, source.iZ);
	}

	private static int positionToCell(int coordinate) {
		return coordinate >> 3;
	}

	public static void resizeLookups(int newSize) {
		lookups = new LookupEntry[newSize];
		startIndices = new int[newSize];
	}

	public static void resetLookups() {
		resizeLookups(DEFAULT_SIZE);
	}

	public static void computeLookup(Collection<LightSource> sources) {
		if(sources.isEmpty()) return;

		Iterator<LookupEntry> it = computeLookupIterator(sources);
		int i = 0;
		while (it.hasNext()) {
			lookups[i] = it.next();
			i++;
		}

		for (i = 0; i < lookups.length; i++) {
			LookupEntry entry = lookups[i];
			if (entry == null) break;

			int key = entry.cellKey();
			int previousKey = i == 0 ? Integer.MAX_VALUE : lookups[i - 1].cellKey();
			if (key != previousKey) startIndices[key] = i;
		}
	}

	private static Iterator<LookupEntry> computeLookupIterator(Collection<LightSource> sources) {
		if(sources.size() > lookups.length * .95f) {
			resizeLookups(lookups.length * 2);
			return computeLookupIterator(sources);
		}

		Arrays.fill(lookups, null);
		Arrays.fill(startIndices, Integer.MAX_VALUE);
		return sources.stream().
				limit(lookups.length).
				map(LookupEntry::of).
				sorted().iterator();
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

	private record LookupEntry(int cellKey, LightSource source) implements Comparable<LookupEntry> {
		static LookupEntry of(LightSource l) {
			return new LookupEntry(hashAt(l), l);
		}

		@Override
		public int compareTo(@NotNull LookupEntry o) {
			return Integer.compare(this.cellKey, o.cellKey);
		}
	}
}
