package cz.zcu.laps.buildingmodel;

import org.apache.commons.cli.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Strelec on IV, 2018 (strelec@ntis.zcu.cz)
 * @author jmacura, strelec
 * @version 1.00 &mdash; 2019
 */
public class TestSimulation {

    private static final String OUTPUT_OPTION = "outputFolder";
    private static final String JSON_OPTION = "modelFile";
    private static final String YAML_OPTION = "paramsFile";
    private static final String HELP_OPTION = "help";

    /**
     * "Definition" stage of command-line parsing with Apache Commons CLI.
     * @return Definition of command-line options.
     */
    private static Options generateOptions() {
        final Option outOption = Option.builder("o")
                .required(false)
                .hasArg()
                .longOpt(OUTPUT_OPTION)
                .desc("Folder where the output files will be saved.")
                .build();
        final Option fileOption = Option.builder("m")
                .required()
                .longOpt(JSON_OPTION)
                .hasArg()
                .desc("Path to the JSON file with abstract model of the building.")
                .build();
        final Option paramsOption = Option.builder("p")
                .required(false)
                .hasArg()
                .longOpt(YAML_OPTION)
                .desc("Path to the YAML file with simulation parameters.")
                .build();
        final Option helpOption = Option.builder("h")
                .required(false)
                .longOpt(HELP_OPTION)
                .desc("Prints this help.")
                .build();
        final Options options = new Options();
        options.addOption(outOption);
        options.addOption(fileOption);
        options.addOption(paramsOption);
        options.addOption(helpOption);
        return options;
    }

    /**
     * "Parsing" stage of command-line processing demonstrated with
     * Apache Commons CLI.
     * @param options Options from "definition" stage.
     * @param commandLineArguments Command-line arguments provided to application.
     * @return Instance of CommandLine as parsed from the provided Options and
     *    command line arguments; may be {@code null} if there is an exception
     *    encountered while attempting to parse the command line options.
     */
    private static CommandLine generateCommandLine(final Options options, final String[] commandLineArguments) {
        final CommandLineParser cmdLineParser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
        }
        catch (ParseException parseException) {
            System.out.println(
                    "ERROR: Unable to parse command-line arguments "
                            + Arrays.toString(commandLineArguments) + " due to: "
                            + parseException);
            printHelp(options);
            System.exit(0);
        }
        return commandLine;
    }

    /**
     * Conversion of input YAML parametric file into SimulationParams structure.
     * @param filename name of YAML file parsed from the command line params
     * @return Instance of SimulationParams which can be later fed into ModelSimulator's constructor.
     */
    private static SimulationParams parseSimulationParams(String filename) {
        Yaml yaml = new Yaml(new Constructor(SimulationParams.class));
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filename);
        }
        catch (IOException e) {
            System.out.println("The file '" + filename + "' was not found!");
            //e.printStackTrace();
        }
        return yaml.load(inputStream);
    }

    /**
     * Generate help information with Apache Commons CLI.
     *
     * @param options Instance of Options to be used to prepare help formatter.
     */
    private static void printHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "java -jar BuildingModel.jar";
        final String usageHeader = "BuildingModel, application for a Heat Transfer Simulation";
        final String usageFooter = "";
        System.out.println("\n==== HELP ====");
        formatter.printHelp(syntax, usageHeader, options, usageFooter, true);
    }

    public static void main(String[] args) {
        // =============================
        //  Get command line parameters
        // =============================
        for (String s : args) {
            if (s.equals("-h") || s.equals("--help")) {
                printHelp(generateOptions());
                System.exit(0);
            }
        }
        CommandLine commandLine = generateCommandLine(generateOptions(), args);
        final String outFolder = commandLine.getOptionValue(OUTPUT_OPTION);
        final String jsonFileName = commandLine.getOptionValue(JSON_OPTION);
        final String yamlFileName = commandLine.getOptionValue(YAML_OPTION);
        //out.println("The file '" + jsonFileName + "' was provided, output is set to '" + outFolder + "' and YAML is '" + yamlFileName + "'.");

        // =============================
        //         Create model
        // =============================
        System.out.println("Reading the input model file...");
        ModelReader mr = new ModelReader(jsonFileName);
        mr.readFile();
        System.out.println("Model file loaded successfully. Creating simulation model...");
        SimulationParams sp;
        HashMap<String, AbstractElement> model;
        if (yamlFileName != null) {
            sp = parseSimulationParams(yamlFileName);
            model = mr.createModel(sp);
        } else {
            // Use defaults
            sp = null;
            model = mr.createModel();
        }
        System.out.println("Simulation model created. Preparing simulation...");

        // =============================
        //     Set-up the simulation
        // =============================
        ModelSimulator ms;
        if (yamlFileName != null) {
            ms = new ModelSimulator(model, sp);
        } else {
            // Use defaults
            ms = new ModelSimulator(model);
            //double samplingPeriod = 15 * 60; // min x 60 [s]
        }
        System.out.println("Simulation prepared. Simulating...");

        // =============================
        //           Simulate
        // =============================
        SimulationLog simulation = ms.simulate();
        System.out.println("Simulation finished. Writing log to file...");

        // =============================
        //     Save results to file
        // =============================
        FileWriter fw;
        try {
            for (HashMap.Entry<String, ArrayList<SimulationEvent>> room : simulation.entrySet()) {
                String roomId = room.getKey();
                String fName = "HTO_" + roomId + ".json";
                File outputFile;
                if (outFolder != null) {
                    outputFile = new File(new File(outFolder), fName);
                } else {
                    outputFile = new File(fName);
                }
                fw = new FileWriter(outputFile);
                simulation.toJSON(roomId).writeJSONString(fw);
                fw.close();
                System.out.println("Output saved into the file " + fName + ".");
            }
        }
        catch (IOException ioe) {
            System.out.println("Writing the file fucked up.");
        }


        // Specification of rooms' thermal capacitance
        //RoomModel room1 = new RoomModel(initialRoomTemperature, samplingPeriod, heatCapacitance);
        // Add heat capacitance as constructor's parameter will be more efficient way than specify
        // it in separate variable as in previous case
        //RoomModel room2 = new RoomModel(initialRoomTemperature, samplingPeriod, 6092*1e3);


        // =============================
        //     Create room instances
        // =============================
        //Heat capacitance C [J/°C] C = c * rho * V
        //double heatCapacitance = 6092*1e3; // Different for each room based on given volume!!!

        // ======================================
        //     Link rooms with other elements
        // ======================================

        // Define ambient (i.e. outside) temperature element. All envelope rooms are connected with this element
        //ConstantThermalSource ambientTemperature = new ConstantThermalSource(20); // For sake of simplicity,
                                                                                       // a constant ambient temperature is used.
                                                                                       // It will be replaced in future by TimeSerieThermalSource.

        // Rooms with adjacent elements
        // Linking of the room with ambient temperature
        //double conductivity = 0.4625 * 1e3; // Illustrative instantiation
        //room1.addAdjacentConductiveElement(new AdjacentConductiveElement(conductivity, ambientTemperature));

        // Interconnection of two rooms
        // Preferred instantiation
        //room1.addAdjacentConductiveElement(new AdjacentConductiveElement(0.6*1e3, room2));
        //room2.addAdjacentConductiveElement(new AdjacentConductiveElement(0.6*1e3, room1));



        // ======================================
        //     Simulation (example)
        // ======================================
        // Simple simulation loop
        // Will be replaced by a simulation class. Not important at the moment
        //for (int i = 0; i < 96; i++) {
        //    System.out.println("Tr1: " + room1.getValue() + "Tr2: " + room2.getValue() + "\tTout: " + ambientTemperature.value);
        //    room1.computeNextStep();
        //    room2.computeNextStep();
        //    room1.update();
        //    room2.update();
        //}
    }
}
