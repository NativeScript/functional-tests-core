package functional.tests.core.mobile.device;

import functional.tests.core.enums.EmulatorState;

/**
 * Emulator info.
 */
public class EmulatorInfo {

    public String id;
    public String name;
    public String platformVersion;
    public EmulatorState state;
    public long usedFrom;

    public EmulatorInfo(String id, String name, String platformVersion, EmulatorState state, long usedFrom) {
        this.id = id;
        this.name = name;
        this.platformVersion = platformVersion;
        this.state = state;
        this.usedFrom = usedFrom;
    }
}
