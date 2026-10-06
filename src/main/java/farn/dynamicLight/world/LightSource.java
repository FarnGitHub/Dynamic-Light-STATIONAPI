package farn.dynamicLight.world;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;


public class LightSource
{
	boolean isLit = false;
	public double posX;
	public double posY;
	public double posZ;
	public int iX;
	public int iY;
	public int iZ;
	private int brightness = 15;
	private int range = brightness * 2 + 1;
	float[] cache = new float[range * range * range];
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

    public void setState(World world, boolean flag)
    {
		if(this.isLit != flag) {
			this.isLit = flag;
			this.markDirty(world, true);
		}
    }

    public void setPos(World world, double x, double y, double z)
    {
            posX = x;
            posY = y;
            posZ = z;
            iX = (int)posX;
            iY = (int)posY;
            iZ = (int)posZ;
			markDirty(world);
    }

	public float getLight(int x, int y, int z)
	{
		if (isLit && !notWorkUnderwater())
		{		
			int diffX = x - iX + brightness;
			int diffY = y - iY + brightness;
			int diffZ = z - iZ + brightness;
			
			if ((diffX >= 0) && (diffX < range) && (diffY >= 0) && (diffY < range) && (diffZ >= 0) && (diffZ < range))
			{
				return cache[(diffX * range * range + diffY * range + diffZ)];
			}
		}
		return 0.0F;
	}

	@SuppressWarnings("all")
	private boolean notWorkUnderwater()
	{
		return (!worksUnderwater && target.isInFluid(Material.WATER));
	}

	private void markDirty(World var1)
	{
		markDirty(var1, false);
	}

    private void markDirty(World world, boolean forceUpdate)
    {
        double XDiff = posX - iX;
        double YDiff = posY - iY;
        double ZDiff = posZ - iZ;
        int index = 0;
		if (System.currentTimeMillis() < this.updateTime+100L && !forceUpdate) return;

        for(int i = -brightness; i <= brightness; i++)
        {
            for(int j = -brightness; j <= brightness; j++)
            {
                for(int k = -brightness; k <= brightness; k++)
                {
					int blockX = i + iX;
					int blockY = j + iY;
                    int blockZ = k + iZ;
                    int blockID = world.getBlockId(blockX, blockY, blockZ);
                    if(blockID != 0 && Block.BLOCKS[blockID].isFullCube())
                    {
                        cache[index++] = 0.0F;
                        continue;
                    }
                    float distance = (float)(Math.abs((i + 0.5D) - XDiff) + Math.abs((j + 0.5D) - YDiff) + Math.abs((k + 0.5D) - ZDiff));
                    if(distance <= (float) brightness)
                    {
                        cache[index++] = (float) brightness - distance;
                    }
					else
                    {
                        cache[index++] = 0.0F;
                    }
                }
            }
        }
		world.setBlocksDirty(
				this.iX-this.brightness,
				this.iY-this.brightness,
				this.iZ-this.brightness,
				this.iX+this.brightness,
				this.iY+this.brightness,
				this.iZ+this.brightness);
		this.updateTime = System.currentTimeMillis();
	}
	
	public void setBrightness(int i)
	{
		brightness = i;
	}
	
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
