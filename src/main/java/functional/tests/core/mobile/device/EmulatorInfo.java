package functional.tests.core.mobile.device;

import functional.tests.core.enums.EmulatorState;

/**
 * Emulator info.
 */
public class EmulatorInfo {

    public String id;
    public String name;
    public EmulatorState state;
    public long usedFrom;

    public EmulatorInfo(String id, String name, EmulatorState state, long usedFrom) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.usedFrom = usedFrom;
    }
}
