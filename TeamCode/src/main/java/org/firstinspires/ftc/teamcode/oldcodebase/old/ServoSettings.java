package org.firstinspires.ftc.teamcode.oldcodebase.old;

public class ServoSettings {

    private double elbowPos;
    private double armPos;

    public ServoSettings setElbowPos(double elbowPos) {
        this.elbowPos = elbowPos;
        return this;
    }

    public ServoSettings setArmPos(double armPos) {
        this.armPos = armPos;
        return this;
    }


    public double getElbowPos() {
        return elbowPos;
    }

    public double getArmPos() {
        return armPos;
    }

}