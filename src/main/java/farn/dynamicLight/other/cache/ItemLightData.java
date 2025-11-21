package farn.dynamicLight.other.cache;

public class ItemLightData {
    public final int brightness;
    public final int range;
    public final int deathAge;
    public final boolean underwater;

    public ItemLightData(int brightness, int range, int deathAge, boolean underwater) {
        this.brightness = brightness;
        this.range = range;
        this.deathAge = deathAge;
        this.underwater = underwater;
    }
}
