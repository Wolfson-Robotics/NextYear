package org.firstinspires.ftc.teamcode.handler;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.GeneralUtils;

public class ServoHandler extends HardwareComponentHandler<Servo> {

    private double minPos = -1;
    private double maxPos = 1;

    private double driveIncrement = 0.01;
    private long speed = 20;


    public ServoHandler(Servo device) {
        super(device);
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

    public void setDriveIncrement(double driveIncrement) {
        this.driveIncrement = driveIncrement;
    }
    public void setSpeed(long speed) {
        this.speed = speed;
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
