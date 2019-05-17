 package cz.zcu.laps.buildingmodel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The {@code SimulationLog} class instances represent a logbook of each event during one simulation loop.
 *
 * @author jmacura
 * @version 0.00.0000 &mdash; 2019
 */
public class SimulationLog extends HashMap<String, ArrayList<SimulationEvent>> {

    public SimulationLog() {
        super();
    }

    /**
     * Adds new entry to this log.
     * @param roomId String with the ID of the room
     * @param simEvt SimulationEvent an event which happened
     */
    public void putEvent(String roomId, SimulationEvent simEvt) {
        ArrayList<SimulationEvent> simList;
        if (this.containsKey(roomId)) {
            simList = this.get(roomId);
        } else {
            simList = new ArrayList<>();
        }
        simList.add(simEvt);
        this.put(roomId, simList);
    }

    /**
     * Converts the log for one room into a writable JSONObject
     * @param roomId String with the ID of the room for which JSONObject should be created
     * @return JSONObject representation of the log for {@code roomId} room in the O&M standard
     */
    public JSONObject toJSON(String roomId) {
        /* Create an output JSON object and fill in the static properties */
        JSONObject out = new JSONObject();
        // property "id"
        out.put("id", "temperature_" + roomId);
        out.put("type", "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_DiscreteTimeSeriesObservation");
        // property "observedProperty"
        JSONObject prop = new JSONObject();
        prop.put("href", "http://environment.data.gov.au/def/property/air_temperature");
        out.put("observedProperty", prop);
        // property "procedure"
        prop = new JSONObject();
        prop.put("href", "http://www.opengis.net/def/waterml/2.0/processType/Simulation");
        out.put("procedure", prop);
        // property "featureOfInterest"
        out.put("featureOfInterest", roomId);
        // property "resultTime"
        out.put("resultTime", Instant.now().toString());
        // property "result"
        JSONObject resultProp = new JSONObject();
        out.put("result", resultProp);
        // subproperty "metadata" (currently empty)
        resultProp.put("metadata", new JSONObject());
        //subproperty "defaultPointMetadata"
        JSONObject defaultPointMetadata = new JSONObject();
        // subsubproperty "interpolationType"
        prop = new JSONObject();
        prop.put("href", "http://www.opengis.net/def/waterml/2.0/interpolationType/Continuous");
        defaultPointMetadata.put("interpolationType", prop);
        // subsubproperty "quality"
        prop = new JSONObject();
        prop.put("href", "http://www.opengis.net/def/waterml/2.0/quality/unchecked");
        defaultPointMetadata.put("quality", prop);
        // subsubproperty "uom"
        prop = new JSONObject();
        prop.put("href", "http://qudt.org/vocab/unit#DegreeCelsius");
        defaultPointMetadata.put("uom", prop);
        resultProp.put("defaultPointMetadata", defaultPointMetadata);

        /* Fill in the properties dependent on the simulation process */
        ArrayList<SimulationEvent> simList = this.get(roomId);
        // property "phenomenonTime"
        prop = new JSONObject();
        prop.put("begin", simList.get(0).getTimestamp().toString());
        prop.put("end", simList.get(simList.size()-1).getTimestamp().toString());
        out.put("phenomenonTime", prop);
        // property

        /* Fill in the values of the simulation itself */
        JSONArray observations = new JSONArray();
        JSONObject room;
        for(SimulationEvent evt : simList) {
            room = new JSONObject();
            prop = new JSONObject();
            prop.put("instant", evt.getTimestamp().toString());
            room.put("time", prop);
            room.put("value", evt.getTemperature());
            observations.add(room);
        }
        resultProp.put("points", observations);
        return out;
    }
}
