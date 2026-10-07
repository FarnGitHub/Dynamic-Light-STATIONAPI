package farn.dynamicLight.world;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.tick.TickScheduler;


public class LightSource{
	boolean isLit = false;
	public double posX;
	public double posY;
	public double posZ;
	public int iX;
	public int iY;
	public int iZ;
	private int brightness = 15;
	private int range = 31;
	byte[] cache = new byte[29791];
	private final Entity target;
	public int currentItemID = 0;
	private boolean worksUnderwater = true;
	public int timer = -1;
	private long updateTime;

    public LightSource(Entity entity)
    {
		target = entity;
    }

    public boolean active()
    {
        return (isLit && target.isAlive() && !notWorkUnderwater());
    }

    public void setState(World world, boolean lit, boolean forceUpdate)
    {
		if(this.isLit != lit || forceUpdate) {
			this.isLit = lit;
			this.markDirty(world, true);
		}
    }

	public void setState(World world, boolean lit)
	{
		setState(world, lit, false);
	}

    public void setPos(World world, double x, double y, double z)
    {
		int flooredX = (int) x;
		int flooredY = (int) y;
		int flooredZ = (int) z;
		if (flooredX != iX || flooredY != iY || flooredZ != iZ) {
			posX = x;
			posY = y;
			posZ = z;
			iX = flooredX;
			iY = flooredY;
			iZ = flooredZ;
			markDirty(world);
		}
    }

	public int getLight(int x, int y, int z)
	{
		if (isLit && !notWorkUnderwater()) {
			int diffX = x - iX + brightness;
			int diffY = y - iY + brightness;
			int diffZ = z - iZ + brightness;

			if ((diffX >= 0) && (diffX < range) && (diffY >= 0) && (diffY < range) && (diffZ >= 0) && (diffZ < range))
			{
				return cache[(diffX * range * range + diffY * range + diffZ)];
			}
		}
		return 0;
	}

	@SuppressWarnings("all")
	private boolean notWorkUnderwater()
	{
		return (!worksUnderwater && target.isInFluid(Material.WATER));
	}

	public void markDirty(World var1)
	{
		markDirty(var1, false);
	}

    public void markDirty(World world, boolean forceUpdate) {
		if (System.currentTimeMillis() < this.updateTime+100L && !forceUpdate) return;
		int index = 0;
		for(int rX = -this.brightness; rX <= this.brightness; ++rX) {
			int x = rX + this.iX;

			for(int rY = -this.brightness; rY <= this.brightness; ++rY) {
				int y = rY + this.iY;

				for(int rZ = -this.brightness; rZ <= this.brightness; ++rZ) {
					int z = rZ + this.iZ;
					double dx = x - posX + 0.5;
					double dy = y - posY + 0.5;
					double dz = z - posZ + 0.5;
					double distanceSquared = dx * dx + dy * dy + dz * dz;
					if (distanceSquared <= range * range) {
						cache[index] = (byte)(brightness - (MathHelper.sqrt(distanceSquared) / range * 30D));
					}
					++index;
				}
			}
		}
		ChunkUpdater.markDirty(this.iX-this.brightness, this.iZ-this.brightness, this.iX+this.brightness, this.iZ+this.brightness);
		this.updateTime = System.currentTimeMillis();
	}
	
	public void setBrightness(int i)
	{
		brightness = i;
	}

	@SuppressWarnings("unused")
	public int getBrightness()
	{
		return brightness;
	}
	
	public void setRange(int i)
	{
		range = i;
	}
	
	public Entity getEntity()
	{
		return target;
	}
	
	public void setWorkUnderWater(boolean works)
	{
		worksUnderwater = works;
	}
	
	public void setTimer(int age)
	{
		timer = age;
	}
	
	public void timerTick()
	{
		timer--;
	}
	
	public boolean hasNoTimer()
	{
		return (timer != -1);
	}
	
	public boolean isDead() {
		return (timer == 0);
	}
}
