package farn.dynamicLight.other.world;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;


public class LightSource
{
	boolean isLit = false;
	float posX;
	float posY;
	float posZ;
	int iX;
	int iY;
	int iZ;
	private int brightness = 15;
	private int range = brightness * 2 + 1;
	float[] cache = new float[range * range * range];
	private Entity target;
	public int currentItemID = 0;
	private boolean worksUnderwater = true;
	public int deathAge = -1;
	public boolean canArmorLit = false;
	private long updateTime;

    public LightSource(Entity entity)
    {
		target = entity;
    }

    public boolean isTorchActive()
    {
        return (isLit && target.isAlive() && !IsPutOutByWater());
    }

    public void setTorchState(World world, boolean flag)
    {
		if(this.isLit != flag) {
			this.isLit = flag;
			this.markBlocksDirty(world, true);
		}
    }

    public void setTorchPos(World world, float x, float y, float z)
    {
            posX = x;
            posY = y;
            posZ = z;
            iX = (int)posX;
            iY = (int)posY;
            iZ = (int)posZ;
			markBlocksDirty(world);
    }

	public float getTorchLight(World world, int x, int y, int z)
	{
		if (isLit && !IsPutOutByWater())
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
	
	private boolean IsPutOutByWater()
	{
		return (!worksUnderwater && target.isInFluid(Material.WATER));
	}

	private void markBlocksDirty(World var1)
	{
		markBlocksDirty(var1, false);
	}

    private void markBlocksDirty(World world, boolean forceUpdate)
    {
        float XDiff = posX - iX;
        float YDiff = posY - iY;
        float ZDiff = posZ - iZ;
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
	
	public void SetTorchBrightness(int i)
	{
		brightness = i;
	}
	
	public int GetTorchBrightness()
	{
		return brightness;
	}
	
	public void SetTorchRange(int i)
	{
		range = i;
	}
	
	public Entity GetTorchEntity()
	{
		return target;
	}
	
	public void SetWorksUnderwater(boolean works)
	{
		worksUnderwater = works;
	}
	
	public void setDeathAge(int age)
	{
		deathAge = age;
	}
	
	public void doAgeTick()
	{
		deathAge--;
	}
	
	public boolean hasDeathAge()
	{
		return (deathAge != -1);
	}
	
	public boolean hasReachedDeathAge()
	{
		return (deathAge == 0);
	}
}
