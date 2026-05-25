package net.mat0u5.lifeseries.utils.interfaces;

public interface IHungerManager {
    int ls$getFoodLevel();
    float ls$getSaturationLevel();

    void ls$setFoodLevel(int foodLevel);
    void ls$setSaturationLevel(float saturationLevel);
}
