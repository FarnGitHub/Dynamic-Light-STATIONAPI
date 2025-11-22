package farn.dynamicLight.other.cache;

public class ItemLightData {
    public int brightness;
    public int range;
    public int deathAge;
    public boolean underwater;

    public int itemId;

    public ItemLightData(int id, int brightness, int range, int deathAge, boolean underwater) {
        this.itemId = id;
        this.brightness = brightness;
        this.range = range;
        this.deathAge = deathAge;
        this.underwater = underwater;
    }
}
