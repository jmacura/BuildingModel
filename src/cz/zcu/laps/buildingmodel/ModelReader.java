/* UTF-8 codepage: Příliš žluťoučký kůn úpěl ďábelské ódy.
 */

package cz.zcu.laps.buildingmodel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * The {@code ModelReader} class instances represent a single BuildingModel corresponding to a single JSON input file.
 *
 * @author jmacura
 * @version 1.00 &mdash; 2019
 */
public class ModelReader {
    private String fileName;
    private JSONObject inputJson;
    private HashMap<String, AbstractElement> model;


    public ModelReader(String fileName)
    {
        this.fileName = fileName;
        this.model = new HashMap<>();
    }

    /**
     * Reads and parses the input JSON file into a JSONObject.
     * @return JSONObject with parsed building parameters from the input file.
     */
    public JSONObject readFile() {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(new FileReader(fileName));
        }
        catch (IOException e) {
            System.out.println("The file '" + fileName + "' was not found!");
            e.printStackTrace();
        }
        catch (ParseException e) {
            System.out.println("The file '" + fileName + "' is not a valid JSON file!");
            e.printStackTrace();
        }
        this.inputJson = json;
        return json;
    }

    /**
     * From JSONObject read by {@code readFile()} method, this method creates a model by instantiating
     * {@code AbstractElement} subclasses.
     * @param params Parameters of the simulation like initial temperature or sampling period.
     * @return HashMap<String, AbstractElement>, where String serves as an ID of the element.
     */
    public HashMap<String, AbstractElement> createModel(SimulationParams params) {
        double initialRoomTemperature = params.getInitialTemperature(); // [°C]
        double samplingPeriod = (double) params.getSamplingPeriodInSeconds(); // min x 60 [s]
        double c = SpecificHeat.AIR_DRY.getValue(); // Different for each room based on given volume!!! Not volume but weight!!
        double rho = 1.29; // [kg/m^3] for air

        //Creating new rooms
        JSONArray rooms = (JSONArray) this.inputJson.get("rooms");
        for (Object element : rooms) {
            JSONObject room = (JSONObject) element;
            String id = (String) room.get("id");
            double volume = (double) room.get("volume"); // [m^3]
            // TODO: heatCapacitance from file
            double heatCapacitance = c*volume*rho; //6092*1e3;
            //System.out.println(heatCapacitance);
            this.model.put(id, new RoomModel(initialRoomTemperature, samplingPeriod, heatCapacitance));
        }

        //Creating ambient (outdoors) zone
        JSONObject ambientObj = (JSONObject) this.inputJson.get("ambient");
        String id = "-1";
        ConstantThermalSource ambientTemperature = null;
        if ((Boolean) ambientObj.get("constant")) {
            ambientTemperature = new ConstantThermalSource(params.getOutsideTemperature());
            this.model.put(id, ambientTemperature);
        }

        //Connecting rooms with each other
        JSONArray walls = (JSONArray) this.inputJson.get("walls");
        for (Object element : walls) {
            JSONObject wall = (JSONObject) element;
            // TODO: conductivy from file
            double specificConductivity = SpecificConductivity.CONCRETE_LIGHT.getValue(); //[W/(m*K)]
            double area = (double) wall.get("area"); //[m^2]
            double thickness; //[m]
            double conductivity; // [W/K]
            String idRoomA = (String) wall.get("leftID");
            String idRoomB = (String) wall.get("rightID");
            AbstractElement abstractRoomA = this.model.get(idRoomA);
            AbstractElement abstractRoomB = this.model.get(idRoomB);
            // Both zones are rooms
            if (abstractRoomA instanceof RoomModel && abstractRoomB instanceof RoomModel) {
                thickness = 0.4; //currently not in data => qualified guess
                conductivity = specificConductivity*area/thickness;
                RoomModel roomA = (RoomModel) abstractRoomA;
                RoomModel roomB = (RoomModel) abstractRoomB;
                roomA.addAdjacentConductiveElement(new AdjacentConductiveElement(conductivity, roomB));
                roomB.addAdjacentConductiveElement(new AdjacentConductiveElement(conductivity, roomA));
            // Zone B is ambient
            } else if (abstractRoomA instanceof RoomModel){
                thickness = 0.8; //currently not in data => qualified guess
                conductivity = specificConductivity*area/thickness;
                RoomModel roomA = (RoomModel) abstractRoomA;
                AbstractStateElement roomB = (AbstractStateElement) abstractRoomB;
                roomA.addAdjacentConductiveElement(new AdjacentConductiveElement(conductivity, roomB));
            // Zone A is ambient
            } else if (abstractRoomB instanceof RoomModel){
                thickness = 0.8; //currently not in data => qualified guess
                conductivity = specificConductivity*area/thickness;
                RoomModel roomB = (RoomModel) abstractRoomB;
                AbstractStateElement roomA = (AbstractStateElement) abstractRoomA;
                roomB.addAdjacentConductiveElement(new AdjacentConductiveElement(conductivity, roomA));
            }
        }
        //System.out.println(this.model.get("US201").toString());
        return this.model;
    }

    /**
     * From JSONObject read by {@code readFile()} method, this method creates a model by instantiating
     * {@code AbstractElement} subclasses. This method has default parameters set.
     * @return HashMap<String, AbstractElement>, where String serves as an ID of the element.
     */
    public HashMap<String, AbstractElement> createModel() {
        return createModel(new SimulationParams("15:00", 10, 20));
    }
}
