package org.firstinspires.ftc.teamcode.handler;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

import org.firstinspires.ftc.teamcode.util.IntegerBounds;

import java.util.Map;

public class DcMotorExHandler extends HardwareComponentHandler<DcMotorEx> {

    private Direction direction = Direction.FORWARD;

    private int lowerPos = Integer.MIN_VALUE;
    private int upperPos = Integer.MAX_VALUE;

    private Map<IntegerBounds, Double> speedMap;

    private boolean isPowered = false;
    private int lastPoweredPos = 0;


    public DcMotorExHandler(DcMotorEx device) {
        super(device);
    }


    private void driveMotor(int targetPosition) {
        this.device.setTargetPosition(targetPosition);
        this.device.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.device.setPower(targetPosition > getPosition() ? this.backwardMult() : this.forwardMult());
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

    public void setDirection(Direction direction) {
        this.device.setDirection(direction);
        this.direction = this.device.getDirection();
    }


    private int forwardMult() {
        return this.direction.equals(Direction.FORWARD) ? 1 : -1;
    }
    private int backwardMult() {
        return this.direction.equals(Direction.FORWARD) ? -1 : 1;
    }
    private boolean posWithin() {
        return this.lowerPos <= this.getPosition() && this.getPosition() <= this.upperPos;
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

    public int getPosition() {
        return this.device.getCurrentPosition();
    }

}
