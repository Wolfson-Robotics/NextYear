package org.firstinspires.ftc.teamcode.oldcodebase.old;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.oldcodebase.CustomTelemetryLogger;
import org.firstinspires.ftc.teamcode.oldcodebase.RobotBase;

import java.io.IOException;
import java.util.Date;


/**
 * Gamepad 1 drive trains
 * Gamepad2 Arm
 * author: WolfsonRobotics
 */
//@TeleOp(name = "debugjavabad")
public class DebugJavaBad extends RobotBase {

    double powerFactor = 1.25;
    private CustomTelemetryLogger logger;
    private boolean buttonPressed = false;


    @Override
    public void initMotors() {
        super.initMotors();
        this.setBrakeMotors();
    }


    @Override
    public void runOpMode() {
        initMotors();

        telemetry.addLine("Waiting for start");
        telemetry.update();
        boolean buttonPressed = false;
        //variables for debug
        String moves = "";
        double startposright = rf_drive.getCurrentPosition();
        double startposleft = lf_drive.getCurrentPosition();
        int numberlog = 0;
        boolean debug = false;
        boolean depadPressed = false;
        boolean turn = false;
        int allowOtherMovement = 0;
        debug = true;
        telemetry.addLine("debug on");
        telemetry.update();


        /*
         * Wait for the user to press start on the Driver Station
         */
        try {
            waitForStart();
            if (debug) {
                String fileName = "/sdcard/Logs/log" + new Date().toString() +".txt";
                logger = new CustomTelemetryLogger(fileName);
                telemetry.addData("name of file: ", fileName);
                telemetry.addData("logger null", (logger == null));
                telemetry.update();
            }
            while (opModeIsActive()) {
                if(debug)
                {

                    // Dpad up starts the logging pipeline
                    if(gamepad1.dpad_up) {
                        startposright = rf_drive.getCurrentPosition();
                        startposleft = lf_drive.getCurrentPosition();
                        telemetry.addLine("log start");
                        telemetry.update();
                    }
                    if((!gamepad1.dpad_down && !gamepad1.dpad_right && !gamepad1.dpad_left)  && depadPressed) depadPressed = false;
                    // Dpad down starts logging the movement
                    if(gamepad1.dpad_down && !depadPressed)
                    {
                        depadPressed = true;
                        numberlog++;
                        double rightDif = (rf_drive.getCurrentPosition() - startposright);
                        double leftDif = (lf_drive.getCurrentPosition() - startposleft);
                        logger.logData("log num: " + numberlog);
                        logger.logData("right movement:" + rightDif);
                        logger.logData("left movement:" + leftDif);
                        telemetry.addData("log end", numberlog);
                        telemetry.update();
                        boolean vertical = ((rightDif >= 0));
                        switch (allowOtherMovement)
                        {
                            case 1:
                                moves += "moveBotExact(" + Math.abs(leftDif) + "," + ((leftDif < 0) ? -1 : 1) +",0,0);\nsleep(500);\n";
                            case 2:
                                moves += "moveBotExact(" + Math.abs(rightDif) + ",0,0," + ((rightDif < 0) ? -1 : 1) + ");\nsleep(500);\n";
                            case 3:
                                moves +=  ("turnBot(" + (ticsToDegrees((int)(Math.round(leftDif))) + ");\nsleep(1000);\n"));
                                break;
                        }
                        allowOtherMovement = 0;



                        startposright = rf_drive.getCurrentPosition();
                        startposleft = lf_drive.getCurrentPosition();
                        turn = false;

                    }
                    // Dpad up actually sends the data to the FileWriter
                    if(gamepad1.dpad_right && !depadPressed)
                    {
                        depadPressed = true;
                        logger.logData(moves);
                        telemetry.addLine("logged moves");
                        telemetry.update();

                    }

                    if(gamepad1.right_stick_x != 0)
                    {
                        turn = true;
                        telemetry.addLine("turn");
                        telemetry.update();
                    }
                }

                if(gamepad1.left_stick_y != 0 && allowOtherMovement == 0)
                {
                    allowOtherMovement = 1;
                }
                if(gamepad1.left_stick_x != 0 && allowOtherMovement == 0)
                {
                    allowOtherMovement = 2;
                }
                if(gamepad1.right_stick_x != 0 && allowOtherMovement == 0)
                {
                    allowOtherMovement = 3;
                }
                switch (allowOtherMovement)
                {
                    case 1:
                        moveBot(-gamepad1.left_stick_y, 0,0);
                        break;
                    case 2:
                        moveBot(0, 0, gamepad1.left_stick_x);
                        break;
                    case 3:
                        moveBot(0, (gamepad1.right_stick_x), 0);
                        break;
                }


            }
        } catch (IOException e) {
            telemetry.addData("Error", "IOException: " + e.getMessage());
            telemetry.update();
        } finally {
            if (logger != null) {
                logger.close();
            }
        }
        if (debug && logger != null) {
            logger.close();
            telemetry.addLine("logger close");
            telemetry.update();
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

    private void moveBot(float vertical, float pivot, float horizontal) {
        pivot *= 0.5;
        rf_drive.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb_drive.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf_drive.setPower(powerFactor * (pivot + vertical + horizontal));
        lb_drive.setPower(powerFactor * (pivot + (vertical - horizontal)));

    }

}
