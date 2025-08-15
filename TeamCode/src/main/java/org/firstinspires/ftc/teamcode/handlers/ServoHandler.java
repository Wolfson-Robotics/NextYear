package org.firstinspires.ftc.teamcode.handlers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.GeneralUtils;

public class ServoHandler extends HardwareComponentHandler<Servo> {

    private double minPos = -1;
    private double maxPos = 1;

    private double driveIncrement = 0.01;
    private long speed = 20;

    private static HardwareMap hardwareMap;
    public static void setHardwareMap(HardwareMap hardwareMap) {
        ServoHandler.hardwareMap = hardwareMap;
    }

    public ServoHandler(Servo device) {
        super(device);
        this.startPos = device.getPosition();
    }
    public ServoHandler(String deviceName) {
        this(hardwareMap.get(Servo.class, deviceName));
        this.name = deviceName;
    }

    public void setPosition(double position) {
        if (!posWithin(position)) return;
        setPositionAsync(position);
        while (Math.abs(device.getPosition() - position) > this.speed*0.1) {
            // Same method that idle() in LinearOpMode uses
            Thread.yield();
        }
    }
    public void setPositionAsync(double position) {
        if (!posWithin(position)) return;
        device.setPosition(position);
    }


    public void drivePosition(double targetPosition) {
        if (!posWithin(targetPosition)) return;
        if (Math.abs(getPosition() - targetPosition) <= driveIncrement) {
            return;
        }
        if (device.getPosition() < targetPosition) {
            this.setPosition(getPosition() + driveIncrement);
        } else {
            this.setPosition(getPosition() - driveIncrement);
        }
        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void setPower(double power) {
        if (power == 0) return;
        if (power < 0) {
            setPositionAsync(getPosition() - driveIncrement);
        } else {
            setPositionAsync(getPosition() + driveIncrement);
        }
    }

    @Override
    public void control(boolean back, boolean forward) {
        if (back && forward) return;
        if (back) this.setPower(-1);
        if (forward) this.setPower(1);
    }

    public void toggleDual(boolean min, boolean max) {
        if (min && max) return;
        if (min) min();
        if (max) max();
    }
    public void toggle(boolean toggle) {
        if (!toggle) return;
        if (atMin()) {
            max();
            return;
        }
        min();
    }



    public boolean posWithin(double position) {
        return minPos <= position && position <= maxPos;
    }
    public void min() {
        device.setPosition(minPos);
    }
    public void max() {
        device.setPosition(maxPos);
    }


    public void scaleRange(double min, double max) {
        device.scaleRange(min, max);
        this.speed = (long) GeneralUtils.convertScale(speed, minPos, maxPos, min, max);
        this.minPos = min;
        this.maxPos = max;
    }
    public void setDirection(Servo.Direction direction) {
        device.setDirection(direction);
    }


    public void setDriveIncrement(double driveIncrement) {
        this.driveIncrement = driveIncrement;
    }
    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public boolean atMin() {
        return this.getPosition() == this.minPos;
    }
    public boolean atMax() {
        return this.getPosition() == this.maxPos;
    }
    public double getPosition() {
        return device.getPosition();
    }
    public double getMin() {
        return this.minPos;
    }
    public double getMax() {
        return this.maxPos;
    }

}
