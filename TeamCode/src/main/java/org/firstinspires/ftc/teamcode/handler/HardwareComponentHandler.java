package org.firstinspires.ftc.teamcode.handler;

import com.qualcomm.robotcore.hardware.HardwareDevice;

public abstract class HardwareComponentHandler<T extends HardwareDevice> {

    protected final T device;

    public HardwareComponentHandler(T device) {
        this.device = device;
    }

    public abstract void setPower(double power);
    public abstract void setPosition(double position);


    public void control(double input) {
        if (input == 0) return;
        this.setPower(input);
    }
    public void control(boolean back, boolean forward) {
        if (back && forward) return;
        if (!back && !forward) this.setPower(0);
        if (back) this.setPower(-1);
        if (forward) this.setPower(1);
    }


    @Deprecated
    public T getDevice() {
        return this.device;
    }

}
