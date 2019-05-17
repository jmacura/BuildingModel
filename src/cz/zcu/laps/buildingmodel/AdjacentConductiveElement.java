 package cz.zcu.laps.buildingmodel;

/**
 * Created by Strelec on IV, 2018
 */
public class AdjacentConductiveElement {
    // Thermal conductivity [W/°C]
    protected double conductivity = 0;

    // Adjacent state element which value is the temperature
    protected AbstractElement adjacentElement = null;

    public AdjacentConductiveElement() {}

    public AdjacentConductiveElement(double conductivity, AbstractStateElement adjacentElement) {
        this.conductivity = conductivity;
        this.adjacentElement = adjacentElement;
    }

    public double getConductivity() {
        return conductivity;
    }

    public void setConductivity(double conductivity) {
        this.conductivity = conductivity;
    }

    public AbstractElement getAdjacentElement() {
        return adjacentElement;
    }

    public void setAdjacentElement(AbstractStateElement adjacentElement) {
        this.adjacentElement = adjacentElement;
    }
}

