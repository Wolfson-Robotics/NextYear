package org.firstinspires.ftc.teamcode.handler;

import com.qualcomm.robotcore.hardware.Servo;

public class ServoHandler extends HardwareComponentHandler<Servo> {

    private double driveIncrement = 0.01;
    private long speed = 20;

    public ServoHandler(Servo device) {
        super(device);
    }

    public void drivePosition(double targetPosition) {
        if (Math.abs(device.getPosition() - targetPosition) <= this.driveIncrement) {
            return;
        }
        if (device.getPosition() < targetPosition) {
            device.setPosition(device.getPosition() + this.driveIncrement);
        } else {
            device.setPosition(device.getPosition() - this.driveIncrement);
        }
        try {
            Thread.sleep(this.speed);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void scaleRange(double min, double max) {
        this.device.scaleRange(min, max);
    }

    public void setDriveIncrement(double driveIncrement) {
        this.driveIncrement = driveIncrement;
    }
    public void setSpeed(long speed) {
        this.speed = speed;
    }

}
