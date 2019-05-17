 package cz.zcu.laps.buildingmodel;

 import java.time.Instant;
 import java.time.OffsetDateTime;
 import java.time.temporal.ChronoUnit;
 import java.util.HashMap;

/**
 * The {@code ModelSimulator} class instances represent a single simulation
 * running on top of a single {@code ModelReader} model.
 *
 * @author jmacura
 * @version 1.00 &mdash; 2019
 */
public class ModelSimulator {
    private HashMap<String, AbstractElement> model;
    private SimulationLog simuLog;
    private Instant simuTime;
    private final Instant endTime;
    private final long samplingPeriod;

    public ModelSimulator(HashMap<String, AbstractElement> model, SimulationParams params) {
        this.model = model;
        this.simuLog = new SimulationLog();
        this.simuTime = params.getSimulationStartDateTime().toInstant(OffsetDateTime.now().getOffset());
        this.endTime = simuTime.plus(params.getSimulationDurationInDurationClass());
        this.samplingPeriod = params.getSamplingPeriodInSeconds();
    }

    public ModelSimulator(HashMap<String, AbstractElement> model) {
        this.model = model;
        this.simuLog = new SimulationLog();
        this.simuTime = Instant.now();
        this.endTime = simuTime.plus(120, ChronoUnit.MINUTES);
        this.samplingPeriod = 15*60;
    }

    /**
     * Runs the simulation itself. Step precision is defined by samplingPeriod parameter (default 15 minutes).
     * @return SimulationLog which is a log of the events in the simulation
     */
    public SimulationLog simulate() {
        simuLog = new SimulationLog();
        for (HashMap.Entry<String, AbstractElement> room : model.entrySet()) {
            System.out.println(room.getKey() + "/" + room.getValue().getValue());
        }
        System.out.println("Simulation starts...");
        while (simuTime.isBefore(endTime) || simuTime.equals(endTime)) {
            //System.out.println(simuTime + " / " + model.get("US201").getValue());
            for (HashMap.Entry<String, AbstractElement> room : model.entrySet()) {
                this.simuLog.putEvent(room.getKey(), new SimulationEvent(simuTime, room.getValue().getValue()));
                if (!room.getKey().equals("-1")) {
                    room.getValue().computeNextStep();
                    room.getValue().update();
                }
            }
            simuTime = simuTime.plusSeconds(samplingPeriod);
        }
        System.out.println("SimulationLog done");
        for (HashMap.Entry<String, AbstractElement> room : model.entrySet()) {
            System.out.println(room.getKey() + "/" + room.getValue().getValue());
        }
        return this.simuLog;
    }
}
