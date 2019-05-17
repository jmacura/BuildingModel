 package cz.zcu.laps.buildingmodel;

/**
 * Created by Strelec on IV, 2018
 */
public abstract class AbstractDynamicStateElement extends AbstractStateElement {
    // Sampling period [s]
    protected double samplingPeriod = Double.NaN;



    public AbstractDynamicStateElement(double samplingPeriod) {
        this.samplingPeriod = samplingPeriod;
    }

    public AbstractDynamicStateElement(double value, double samplingPeriod) {
        super(value);
        this.samplingPeriod = samplingPeriod;
    }

    public AbstractDynamicStateElement(double value, double nextValue, double samplingPeriod) {
        super(value, nextValue);
        this.samplingPeriod = samplingPeriod;
    }

    public abstract double computeNextStep();

}
