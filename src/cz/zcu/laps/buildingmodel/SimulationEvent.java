package cz.zcu.laps.buildingmodel;

import java.time.Instant;

/**
 * The {@code SimulationEvent} class instances represent a point in time, in which the temperature changes.
 *
 * @author jmacura
 * @version 0.00.0000 &mdash; 2019
 */
public class SimulationEvent {
    private Instant timestamp;
    //private String roomID;
    private double temperature;

    public SimulationEvent(Instant time, double temp) {
        this.timestamp = time;
        //this.roomID = id;
        this.temperature = temp;
    }

    @Override
    public String toString() {
        return "{timestamp: " + this.timestamp + "," +
                //"roomID: " + this.roomID + "," +
                "temperature: " + this.temperature +
                "}";
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    /*public String getRoomID() {
        return roomID;
    }*/

    public double getTemperature() {
        return temperature;
    }
}
