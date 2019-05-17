 package cz.zcu.laps.buildingmodel;

/**
 * Created by Strelec on IV, 2018
 */

public class ConstantThermalSource extends AbstractStateElement {
    public ConstantThermalSource(double value) {
        super(value);
        this.nextValue = value;
    }

    @Override
    public double computeNextStep() {
        return value;
    }

    @Override
    public String toString() {
        return "";
    }
}
