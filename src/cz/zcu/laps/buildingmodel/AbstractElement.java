 package cz.zcu.laps.buildingmodel;

/**
 * Created by Strelec on IV, 2018
 */
public abstract class AbstractElement {
    public abstract double computeNextStep();
    public abstract void update();
    public abstract double getValue();
    public abstract String toString();
}
