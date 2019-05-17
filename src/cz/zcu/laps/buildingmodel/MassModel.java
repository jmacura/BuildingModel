 package cz.zcu.laps.buildingmodel;

import java.util.ArrayList;

/**
 * Created by Strelec on IV, 2018
 */
public class MassModel extends AbstractDynamicStateElement {
    // Heat capacitance C [J/°C] C = c * rho * V
    // c - specific heat [J/kg,°C]
    // rho - density [kg/m3]
    // V - volume [m3]
    protected double heatCapacitance = 0;

    // Adjacent conducting elements (e.g. rooms, another masses)
    protected ArrayList<AdjacentConductiveElement> adjacentConductiveElements = null;


    public MassModel(double samplingPeriod, double heatCapacitance) {
        super(samplingPeriod);
        this.heatCapacitance = heatCapacitance;
    }

    public MassModel(double samplingPeriod, double value, double heatCapacitance) {
        super(samplingPeriod, value);
        this.heatCapacitance = heatCapacitance;
    }

    public MassModel(double samplingPeriod, double value, double heatCapacitance, ArrayList<AdjacentConductiveElement> adjacentConductiveElements) {
        super(samplingPeriod, value);
        this.heatCapacitance = heatCapacitance;
        this.adjacentConductiveElements = adjacentConductiveElements;
    }

    @Override
    public double computeNextStep() {
        double conductivitySum = 0.0;

        for (AdjacentConductiveElement element : adjacentConductiveElements) {
            // sum_i[Gi * (Ti - Tm)]
            conductivitySum += element.getConductivity() * (element.getAdjacentElement().getValue() - value);
        }

        nextValue = value + samplingPeriod/heatCapacitance * conductivitySum;
        return nextValue;
    }

    @Override
    public String toString() {
        return "";
    }
}
