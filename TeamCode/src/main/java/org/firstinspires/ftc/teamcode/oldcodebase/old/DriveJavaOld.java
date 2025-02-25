package org.firstinspires.ftc.teamcode.oldcodebase.old;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Gamepad 1 drive trains
 * Gamepad2 Arm
 * author: WolfsonRobotics
 */
//@TeleOp(name = "Dive1Old")
public class DriveJavaOld extends LinearOpMode {
    private DcMotor right_drive1;
    private DcMotor right_drive2;
    private DcMotor left_drive1;
    private DcMotor left_drive2;
    double powerFactor = 1.25;


    public void initMotors() {
        right_drive1 = hardwareMap.get(DcMotor.class, "right_drive1");
        right_drive2 = hardwareMap.get(DcMotor.class, "right_drive2");
        left_drive1 = hardwareMap.get(DcMotor.class, "left_drive1");
        left_drive2 = hardwareMap.get(DcMotor.class, "left_drive2");

        right_drive1.setDirection(DcMotorSimple.Direction.REVERSE);
        right_drive2.setDirection(DcMotorSimple.Direction.REVERSE);



    }

    @Override
    public void runOpMode() {
        initMotors();

        telemetry.addLine("Waiting for start");
        telemetry.update();
        boolean buttonPressed = false;


        /*
         * Wait for the user to press start on the Driver Station
         */
            waitForStart();
            while (opModeIsActive()) {




                moveBot(-gamepad1.left_stick_y, (gamepad1.right_stick_x), gamepad1.left_stick_x);

            }
    }
    private boolean isButtonPressed(String button) {
        switch (button) {
            case "A":
                return gamepad2.a;
            case "B":
                return gamepad2.b;
            case "X":
                return gamepad2.x;
            case "Y":
                return gamepad2.y;
            case "dpad_up":
                return gamepad2.dpad_up;
            case "dpad_down":
                return gamepad2.dpad_down;
            case "dpad_right":
                return gamepad2.dpad_right;
            case "right_bumper":
                return gamepad2.right_bumper;
            default:
                return false;
        }
    }
    private int ticsToDegrees(int tics)
    {
        int degrees = 0;
        double intCon = 8.727272;
        double robotLength = 13.62;
        double distUnit = (robotLength) / (Math.cos(45));
        degrees = Math.round((float)(((((tics /intCon)*90)/distUnit)/1.75)));
        return degrees;
    }
    private void moveServo(Servo servo, double targetPosition, long speed) {
        if (Math.abs(servo.getPosition() - targetPosition) > 0.01) {
            // Move the servo towards the target position slowly
            if (servo.getPosition() < targetPosition) {
                servo.setPosition(servo.getPosition() + .01);
            } else {
                servo.setPosition(servo.getPosition() - .01);
            }

            // Sleep for a short duration (adjust as needed)
            sleep(speed); // Sleep for 100 milliseconds (adjust for desired speed)
        }

    }

    private void moveBot(float vertical, float pivot, float horizontal) {
        pivot *= 0.6;
        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));

    }

}






