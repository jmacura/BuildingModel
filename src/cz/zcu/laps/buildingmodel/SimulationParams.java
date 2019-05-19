 package cz.zcu.laps.buildingmodel;

 import java.time.Duration;
 import java.time.LocalDateTime;
 import java.time.ZoneId;
 import java.time.format.DateTimeFormatter;
 import java.util.Date;

/**
 * The {@code SimulationParams} class instances represent a set of parameters of the simulation.
 * Its structure correlates with the YAML file, which can be used as an input of the application.
 *
 * @author jmacura
 * @version 1.00 &mdash; 2019
 */
public class SimulationParams {

    // Type conversion is limited by current SnakeYaml capabilities
    private Date simulationStartDate;
    private String simulationStartTime;
    private String simulationDuration;
    private String samplingPeriod;
    private double initialTemperature;
    private double outsideTemperature;

    /**
     * Constructor with all the simulation parameters.
     * @param simulationStartDate Date when the simulation should start in yyyy-mm-dd format
     * @param simulationStartTime Time when the simulation should start in HH:mm:ss format
     * @param simulationDuration Time the simulation should cover in HH:mm:ss format
     * @param samplingPeriod Period in which the simulation will compute next step, in mm:ss format
     * @param initialTemperature Initial room temperature in degrees celsius
     * @param outsideTemperature Constant ambient temperature in degrees celsius
     */
    public SimulationParams(
            Date simulationStartDate,
            String simulationStartTime,
            String simulationDuration,
            String samplingPeriod,
            double initialTemperature,
            double outsideTemperature
    ) {
        this.simulationStartDate = simulationStartDate;
        this.simulationStartTime = simulationStartTime;
        this.simulationDuration = simulationDuration;
        this.samplingPeriod = samplingPeriod;
        this.initialTemperature = initialTemperature;
        this.outsideTemperature = outsideTemperature;
    }

    /**
     * Constructor with only the parameters necessary when creating the model.
     * @param samplingPeriod Period in which the simulation will compute next step, in mm:ss format
     * @param initialTemperature Initial room temperature in degrees celsius
     * @param outsideTemperature Constant ambient temperature in degrees celsius
     */
    public SimulationParams(
            String samplingPeriod,
            double initialTemperature,
            double outsideTemperature
    ) {
        this.samplingPeriod = samplingPeriod;
        this.initialTemperature = initialTemperature;
        this.outsideTemperature = outsideTemperature;
    }

    /**
     * Empty constructor necessary for the SnakeYAML parser.
     */
    public SimulationParams() {};

    public Date getSimulationStartDate() {
        return simulationStartDate;
    }

    public void setSimulationStartDate(Date simulationStartDate) {
        this.simulationStartDate = simulationStartDate;
    }

    public String getSimulationStartTime() {
        return simulationStartTime;
    }

    public void setSimulationStartTime(String simulationStartTime) {
        this.simulationStartTime = simulationStartTime;
    }

    public String getSimulationDuration() {
        return simulationDuration;
    }

    public void setSimulationDuration(String simulationDuration) {
        this.simulationDuration = simulationDuration;
    }

    public String getSamplingPeriod() {
        return samplingPeriod;
    }

    public void setSamplingPeriod(String samplingPeriod) {
        this.samplingPeriod = samplingPeriod;
    }

    public double getInitialTemperature() {
        return initialTemperature;
    }

    public void setInitialTemperature(double initialTemperature) {
        this.initialTemperature = initialTemperature;
    }

    public double getOutsideTemperature() {
        return outsideTemperature;
    }

    public void setOutsideTemperature(double outsideTemperature) {
        this.outsideTemperature = outsideTemperature;
    }

    /**
     * Joins two simulation parameters of start date and start time into single one.
     * @return LocalDateTime of simulationStartDate and simulationStartTime
     */
    public LocalDateTime getSimulationStartDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
        //System.out.println(LocalTime.parse(this.simulationStartTime, DateTimeFormatter.ofPattern("HH:mm:ss")));
        return LocalDateTime.parse(dtf.format(this.simulationStartDate.toInstant()) + "T" + this.simulationStartTime);
    }

    /**
     * Converts the string of simulationDuration from HH:mm:ss into a Duration object.
     * @return simulationDuration as Duration object
     */
    public Duration getSimulationDurationInDurationClass() {
        String[] hms = this.simulationDuration.split(":");
        String durationString = "PT" + hms[0] + "H" + hms[1] + "M" + hms[2] + "S";
        return Duration.parse(durationString);
    }

    /**
     *  Converts the string of samplingPeriod from mm:ss into seconds.
     * @return samplingPeriod in seconds
     */
    public long getSamplingPeriodInSeconds() {
        String[] ms = this.samplingPeriod.split(":");
        return Long.parseLong(ms[0])*60+Long.parseLong(ms[1]);
    }
}
