package farn.dynamicLight.impl;

import farn.dynamicLight.world.light_source.LightSource;

public interface DynamicLightEntity {

    default boolean dynamiclight_shouldAddToLightSource() {
        return false;
    }

    default LightSource dynamiclight_createLightSource() {
        throw new AssertionError();
    }
}
