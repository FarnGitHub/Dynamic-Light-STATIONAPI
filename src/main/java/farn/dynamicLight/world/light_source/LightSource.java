package farn.dynamicLight.world.light_source;

import farn.dynamicLight.world.tick.ChunkUpdater;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

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

    public void setState(boolean lit, boolean forceUpdate)
    {
		if(this.isLit != lit || forceUpdate) {
			this.isLit = lit;
			this.markDirty(true);
		}
    }

	public void setState(boolean lit)
	{
		setState(lit, false);
	}

    public void setPos(double x, double y, double z)
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
			markDirty(false);
		}
    }

	public int getLight(int x, int y, int z)
	{
		if (isLit && !notWorkUnderwater()) {
			double dx = x - posX + 0.5;
			double dy = y - posY + 0.5;
			double dz = z - posZ + 0.5;
			double distanceSquared = dx * dx + dy * dy + dz * dz;
			if (distanceSquared <= range * range) {
				return (int)(brightness - MathHelper.sqrt(distanceSquared));
			}
		}
		return 0;
	}

	@SuppressWarnings("all")
	private boolean notWorkUnderwater()
	{
		return (!worksUnderwater && target.isInFluid(Material.WATER));
	}


    public void markDirty(boolean forceUpdate) {
		if (System.currentTimeMillis() < this.updateTime+100L && !forceUpdate) return;
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
