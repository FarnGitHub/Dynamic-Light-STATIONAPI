package farn.dynamicLight.world.tick;

import farn.dynamicLight.world.light_source.LightSource;
import net.minecraft.block.Block;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class DynamicLightEngine {
	public static final DynamicLightEngine INST = new DynamicLightEngine();

	public final int DEFAULT_SIZE = 128;

	private final Vec3i[] CELL_OFFSETS;
	private LookupEntry[] lookups = new LookupEntry[DEFAULT_SIZE];
	private int[] startIndices = new int[DEFAULT_SIZE];

	private DynamicLightEngine() {
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

	public int getBrightness(BlockView view, int x, int y, int z, int light) {
		int blockId = view.getBlockId(x, y, z);
		if(blockId > 0 && Block.BLOCKS[blockId].isFullCube()) return light;

		int cX = posToCell(x);
		int cY = posToCell(y);
		int cZ = posToCell(z);
		for(Vec3i v : CELL_OFFSETS) {
			int hash = hashCell(cX+v.x, cY+v.y, cZ+v.z);
			int start = startIndices[hash];
			for(int l = start; l < lookups.length; ++l) {
				LookupEntry lookup = lookups[l];
				if(lookup == null || lookup.cellKey != hash) break;

				int torchLight = lookup.source.getLightLevel(x, y, z);
				if(torchLight > light) light = torchLight;
			}
		}

		return Math.min(Math.max(light, 0), 15);
	}

	private int hashCell(int cellX, int cellY, int cellZ) {
		return Math.abs(((cellX + 31) * 19 + cellY) * 41 + cellZ) * 83 & (lookups.length - 1);
	}

	private int hashAt(int x, int y, int z) {
		return hashCell(posToCell(x), posToCell(y), posToCell(z));
	}
	private int hashAt(LightSource source) {
		return hashAt(source.iX, source.iY, source.iZ);
	}

	private int posToCell(int coordinate) {
		return coordinate >> 3;
	}

	public void resizeLookups(int newSize) {
		lookups = new LookupEntry[newSize];
		startIndices = new int[newSize];
	}

	public void resetLookups() {
		resizeLookups(DEFAULT_SIZE);
	}

	public void computeLookup(Collection<LightSource> sources) {
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

	private Iterator<LookupEntry> computeLookupIterator(Collection<LightSource> sources) {
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

	private record LookupEntry(int cellKey, LightSource source) implements Comparable<LookupEntry> {
		static LookupEntry of(LightSource l) {
			return new LookupEntry(INST.hashAt(l), l);
		}

		@Override
		public int compareTo(@NotNull LookupEntry o) {
			return Integer.compare(this.cellKey, o.cellKey);
		}
	}
}
