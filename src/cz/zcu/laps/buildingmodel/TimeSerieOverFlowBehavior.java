 package cz.zcu.laps.buildingmodel;

/**
 * Created by Strelec on IV, 2018
 */

public enum TimeSerieOverFlowBehavior {
    EXCEPTION ((byte) 0),
    HOLD_FIRST ((byte) 1),
    HOLD_LAST ((byte) 2),
    RESET ((byte) 3);

    private final byte value; // index of the element in the array
    public static int COUNT = TimeSerieOverFlowBehavior.values().length;

    TimeSerieOverFlowBehavior(final byte newValue) {
        value = newValue;
    }

    public byte getValue() {
        return value;
    }
}
