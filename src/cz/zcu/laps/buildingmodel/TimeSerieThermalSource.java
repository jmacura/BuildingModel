 package cz.zcu.laps.buildingmodel;

/**
 * Created by Strelec on IV, 2018
 */
public class TimeSerieThermalSource extends AbstractElement {
    // Index of cursor
    protected int index = 0;
    // Increment of the index in the compute next step
    protected int increment = 1;
    // Behavior of the thermal source after end of timeserie is reached
    protected TimeSerieOverFlowBehavior overFlowBehavior = TimeSerieOverFlowBehavior.EXCEPTION;
    // Timeserie
    protected double[] timeserie = null;
    // Sampling period [s]
    protected double samplingPeriod = Double.NaN;


    public TimeSerieThermalSource(double[] timeserie) {
        this.timeserie = timeserie;
    }

    public TimeSerieThermalSource(double[] timeserie, double samplingPeriod) {
        this.timeserie = timeserie;
        this.samplingPeriod = samplingPeriod;
    }

    public TimeSerieThermalSource(TimeSerieOverFlowBehavior overFlowBehavior, double[] timeserie) {
        this.overFlowBehavior = overFlowBehavior;
        this.timeserie = timeserie;
    }

    public TimeSerieThermalSource(TimeSerieOverFlowBehavior overFlowBehavior, double[] timeserie, double samplingPeriod) {
        this.overFlowBehavior = overFlowBehavior;
        this.timeserie = timeserie;
        this.samplingPeriod = samplingPeriod;
    }

    public TimeSerieThermalSource(int index, int increment, TimeSerieOverFlowBehavior overFlowBehavior, double[] timeserie, double samplingPeriod) {
        this.index = index;
        this.increment = increment;
        this.overFlowBehavior = overFlowBehavior;
        this.timeserie = timeserie;
        this.samplingPeriod = samplingPeriod;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public TimeSerieOverFlowBehavior getOverFlowBehavior() {
        return overFlowBehavior;
    }

    public void setOverFlowBehavior(TimeSerieOverFlowBehavior overFlowBehavior) {
        this.overFlowBehavior = overFlowBehavior;
    }

    public double[] getTimeserie() {
        return timeserie;
    }

    public void setTimeserie(double[] timeserie) {
        this.timeserie = timeserie;
    }

    public double getSamplingPeriod() {
        return samplingPeriod;
    }

    public void setSamplingPeriod(double samplingPeriod) {
        this.samplingPeriod = samplingPeriod;
    }

    @Override
    public double computeNextStep() {
        try {
            index += increment;

            if (index >= timeserie.length) {
                switch (overFlowBehavior) {
                    case RESET:
                        index = 0; break;
                    case HOLD_LAST:
                        index = timeserie.length-1; increment = 0; break;
                    case HOLD_FIRST:
                        index = 0; increment = 0; break;
                    default:
                        throw new ArrayIndexOutOfBoundsException();
                }
            }

            return timeserie[index];
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return Double.NaN;
    }

    @Override
    public void update() {}

    @Override
    public double getValue() {
        try {
            return timeserie[index];
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return Double.NaN;
    }

    @Override
    public String toString() {
        return "";
    }
}

