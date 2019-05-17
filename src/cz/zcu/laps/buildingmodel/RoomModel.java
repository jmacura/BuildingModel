package cz.zcu.laps.buildingmodel;

import java.util.ArrayList;

/**
 * Created by Strelec on IV, 2018
 */
public class RoomModel extends AbstractDynamicStateElement {
    // Heat capacitance C [J/°C] C = c * rho * V
    // c - specific heat [J/kg * °C]
    // rho - density [kg/m3]
    // V - volume [m3]
    protected double heatCapacitance = 0;

    // Adjacent conducting elements (e.g. rooms, another internal masses, ambient temperature)
    protected ArrayList<AdjacentConductiveElement> adjacentConductiveElements = null;

    // Internal thermal sources
    protected ArrayList<AbstractElement> thermalSources = null;

    // Mass flow element
    protected ArrayList<MassFlowElement> massFlowSources = null;


    public RoomModel(double value, double samplingPeriod) {
        super(value, samplingPeriod);
    }

    public RoomModel(double value, double samplingPeriod, double heatCapacitance) {
        super(value, samplingPeriod);
        this.heatCapacitance = heatCapacitance;
    }

    public RoomModel(double value, double samplingPeriod, double heatCapacitance, ArrayList<AdjacentConductiveElement> adjacentConductiveElements, ArrayList<AbstractElement> thermalSources, ArrayList<MassFlowElement> massFlowSources) {
        super(value, samplingPeriod);
        this.heatCapacitance = heatCapacitance;
        this.adjacentConductiveElements = adjacentConductiveElements;
        this.thermalSources = thermalSources;
        this.massFlowSources = massFlowSources;
    }

    public double getHeatCapacitance() {
        return heatCapacitance;
    }

    public void setHeatCapacitance(double heatCapacitance) {
        this.heatCapacitance = heatCapacitance;
    }

    public void addAdjacentConductiveElement(AdjacentConductiveElement element) {
        if (adjacentConductiveElements == null) {
            adjacentConductiveElements = new ArrayList<>();
        }

        adjacentConductiveElements.add(element);
    }

    @Override
    public String toString() {
        String s = "{RoomModel\n heatCapacitance: " + this.heatCapacitance + "\n";
        if (this.adjacentConductiveElements != null) {
            s += " adjacentConductiveElements: " + this.adjacentConductiveElements.size() + "\n";
        } else {
            s += " adjacentConductiveElements: 0\n";
        }
        if (this.massFlowSources != null) {
            s += " massFlowSources: " + this.massFlowSources.size() + "\n";
        } else {
            s += " massFlowSources: 0\n";
        }
        if (this.thermalSources != null) {
            s += " thermalSources: " + this.thermalSources.size() + "\n";
        } else {
            s += " thermalSources: 0\n";
        }
        s += "}";
        return s;
    }

    @Override
    public double computeNextStep() {
        try {
            // Sum internal heat sources
            double internalHeatSourcesGain = 0.0;
            if (thermalSources != null) {
                for (AbstractElement element: thermalSources) {
                    internalHeatSourcesGain += element.getValue();
                }
            }

            // Sum adjacent rooms
            double adjacentElementsGain = 0.0;
            if (adjacentConductiveElements!= null) {
                for (AdjacentConductiveElement element: adjacentConductiveElements) {
                    // Gi * (T - Ti)
                    adjacentElementsGain += element.getConductivity()
                                         * (element.getAdjacentElement().getValue() - value);
                }
            }

            // Sum mass flow elements
            double massFlowElementsGain = 0.0;
            if (massFlowSources!= null) {
                for (MassFlowElement element: massFlowSources) {
                    // Gi * (T - Ti)
                    adjacentElementsGain += element.getConductivity()
                            * (element.getElement().getValue() - value);
                }
            }

            nextValue = value + samplingPeriod/heatCapacitance
                       * (internalHeatSourcesGain + adjacentElementsGain + massFlowElementsGain);

            return nextValue;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return Double.NaN;
    }
}
