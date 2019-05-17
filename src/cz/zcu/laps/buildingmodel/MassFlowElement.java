 package cz.zcu.laps.buildingmodel;

/**
 * Created by Strelec on IV, 2018
 */
public class MassFlowElement {
    // Specific heat at constant pressure [J/kg]
    protected double specificHeat  = Double.NaN;

    // Density of flow medium [kg/m3]
    protected double density = Double.NaN;

    // Volume flow [m3]
    protected double massFlow = Double.NaN;

    // Mass Flow element
    protected AbstractElement element = null;

    public MassFlowElement(double specificHeat, double density, double massFlow, AbstractElement element) {
        this.specificHeat = specificHeat;
        this.density = density;
        this.massFlow = massFlow;
        this.element = element;
    }

    public double getSpecificHeat() {
        return specificHeat;
    }

    public void setSpecificHeat(double specificHeat) {
        this.specificHeat = specificHeat;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getMassFlow() {
        return massFlow;
    }

    public void setMassFlow(double massFlow) {
        this.massFlow = massFlow;
    }

    public AbstractElement getElement() {
        return element;
    }

    public void setElement(AbstractElement element) {
        this.element = element;
    }

    public double getConductivity() {
        return (double) specificHeat * density * massFlow;
    }
}
