package org.firstinspires.ftc.teamcode.handler;

import com.qualcomm.robotcore.hardware.HardwareDevice;

public class HardwareComponentHandler<T extends HardwareDevice> {

    protected final T device;

    public HardwareComponentHandler(T device) {
        this.device = device;
    }

    @Deprecated
    public T getDevice() {
        return this.device;
    }

}
