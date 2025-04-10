package org.firstinspires.ftc.teamcode.handlers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

import org.firstinspires.ftc.teamcode.util.IntegerBounds;

import java.util.Map;

public class DcMotorExHandler extends HardwareComponentHandler<DcMotorEx> {

    private int lowerPos = Integer.MIN_VALUE;
    private int upperPos = Integer.MAX_VALUE;

    private Map<IntegerBounds, Double> speedMap;

    private final int startPos = 0;
    private boolean isPowered = false;
    private double lastPoweredPos = 0;


    public DcMotorExHandler(DcMotorEx device) {
        super(device);
        device.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    private void driveMotor(double targetPosition, double power) {
        this.device.setTargetPosition((int) targetPosition);
        this.device.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.device.setPower(targetPosition > getPosition() ? power : -power);
    }
    private void driveMotor(double targetPosition) {
        driveMotor(targetPosition, 1);
    }

    public boolean limit() {
        if (posWithin()) {
            return false;
        }
        if (getPosition() < this.lowerPos) {
            driveMotor(this.lowerPos);
        } else if (getPosition() > this.upperPos) {
            driveMotor(this.upperPos);
        }
        return true;
    }

    public void setPower(double power) {
        if (power == 0) {
            if (this.isPowered) {
                limit();
                this.isPowered = false;
                this.lastPoweredPos = getPosition();
            }
            driveMotor(this.lastPoweredPos);
        }

        this.isPowered = true;
        if (limit()) {
            return;
        }
        if (!this.speedMap.isEmpty()) {
            this.speedMap.forEach((bounds, mult) -> {
                if (bounds.inBetweenClosed(getPosition())) this.device.setPower(power * mult);
            });
        } else {
            this.device.setPower(power);
        }
    }

    public void setPosition(double position) {
        this.setPosition(position, 1);
    }
    public void setPosition(double position, double power) {
        if (!posWithin(position)) return;
        driveMotor((int) position, power);
        while (getPosition() != position) {
            // Same method that idle() in LinearOpMode uses
            Thread.yield();
        }
    }

    public void resetEncoder() {
        this.device.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
    }
    public void setDirection(Direction direction) {
        this.device.setDirection(direction);
    }


    public boolean posWithin(double position) {
        return this.lowerPos <= position && position <= this.upperPos;
    }
    private boolean posWithin() {
        return posWithin(getPosition());
    }


    public void setSpeedMap(Map<IntegerBounds, Double> speedMap) {
        this.speedMap = speedMap;
    }
    public void setPositionBounds(int lowerPos, int upperPos) {
        this.lowerPos = lowerPos;
        this.upperPos = upperPos;
    }

    public double getPower() {
        return this.device.getPower();
    }

    public double getPosition() {
        return this.device.getCurrentPosition();
    }

}
