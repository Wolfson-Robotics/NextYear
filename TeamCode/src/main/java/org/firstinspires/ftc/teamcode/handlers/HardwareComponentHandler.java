package org.firstinspires.ftc.teamcode.handlers;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.util.Optional;

public abstract class HardwareComponentHandler<T extends HardwareDevice> {

    protected String name;
    protected final T device;
    protected double startPos;

    public HardwareComponentHandler(T device) {
        this.device = device;
    }

    public abstract void setPower(double power);
    public abstract void setPosition(double position);
    public abstract double getPosition();
    public synchronized double getRelativePosition() {
        return getPosition() - getStartPosition();
    }
    public double getStartPosition() {
        return this.startPos;
    }


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

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HardwareComponentHandler)) return false;
        return device.equals(((HardwareComponentHandler<?>) obj).device);
    }
    @NonNull
    @Override
    public String toString() {
        return Optional.ofNullable(name).orElse(device.toString());
    }
    @Override
    public int hashCode() {
        return device.hashCode();
    }

}
