package farn.dynamicLight.world.light_source;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class LightSource{
	private boolean isLit = false;
	public double posX;
	public double posY;
	public double posZ;
	public int iX;
	public int iY;
	public int iZ;
	private byte brightness = 15;
	private int squaredRange = 225;
	private final Entity target;
	private boolean worksUnderwater = true;
	private long updateTime;

    public LightSource(Entity entity) {
		target = entity;
    }

	public LightSource(Entity entity, boolean lit) {
		this(entity);
		this.isLit = lit;
	}

	public void tick() {
		if (this.active()) {
			this.setPos(target.x, target.y, target.z);
		}
	}

    public boolean active(){
        return (isLit && target.isAlive() && workUnderwater());
    }

	public void setBrightness(int brightness){
		this.brightness = (byte)brightness;
		this.squaredRange = brightness * brightness;
	}

	@SuppressWarnings("unused")
	public int getBrightness() {
		return brightness;
	}

	public Entity getEntity() {
		return target;
	}

	public void setWorkUnderWater(boolean works) {
		worksUnderwater = works;
	}

	private boolean workUnderwater() {
		return worksUnderwater || !target.isInFluid(Material.WATER);
	}

	public int getLightLevel(int x, int y, int z) {
		if (active()) {
			double dx = x - posX + 0.5;
			double dy = y - posY + 0.5;
			double dz = z - posZ + 0.5;
			double distanceSquared = dx * dx + dy * dy + dz * dz;
			if (distanceSquared <= squaredRange) {
				return (int)(brightness - Math.sqrt(distanceSquared));
			}
		}
		return 0;
	}

	public void setLit(boolean lit) {
		if(this.isLit != lit) {
			this.isLit = lit;
			this.markDirty(true);
		}
	}

    public void setPos(double x, double y, double z) {
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
			this.markDirty(false);
		}
    }

    public void markDirty(boolean force) {
		if (!force && System.currentTimeMillis() < this.updateTime+100L) return;
		Minecraft.INSTANCE.world.setBlocksDirty(
				this.iX-this.brightness,
				this.iY-this.brightness,
				this.iZ-this.brightness,
				this.iX+this.brightness,
				this.iY+this.brightness,
				this.iZ+this.brightness);
		this.updateTime = System.currentTimeMillis();
	}
}
