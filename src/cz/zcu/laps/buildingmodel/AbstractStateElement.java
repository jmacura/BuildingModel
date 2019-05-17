 package cz.zcu.laps.buildingmodel;

/**
 * Created by Strelec on IV, 2018
 */
public abstract class AbstractStateElement extends AbstractElement {
    // Current value
    protected double value = Double.NaN;

    // Next step value (in the case of dynamical system)
    protected double nextValue = Double.NaN;

    public AbstractStateElement() {}


    public AbstractStateElement(double value) {
        this.value = value;
    }

    public AbstractStateElement(double value, double nextValue) {
        this.value = value;
        this.nextValue = nextValue;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setNextValue(double nextValue) {
        this.nextValue = nextValue;
    }

    @Override
    public double getValue() {
        return value;
    }

    public double getNextValue() {
        return nextValue;
    }

    public void update() {
        this.value = this.nextValue;
    }
}
