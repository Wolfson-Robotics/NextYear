package org.firstinspires.ftc.teamcode.oldcodebase.teleop;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.oldcodebase.CustomTelemetryLogger;
import org.firstinspires.ftc.teamcode.oldcodebase.RobotBase;
import org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionCodeSerializer;
import org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants;
import org.firstinspires.ftc.teamcode.oldcodebase.old.ServoSettings;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Gamepad 1 drive trains
 * Gamepad2 Arm
 * author: WolfsonRobotics
 */
@TeleOp(name = "debugjava")
public class DebugJava extends RobotBase {
    private ServoSettings servoSettings = new ServoSettings();
    private Map<String, ServoSettings> servoPositions = new HashMap<>();
    private CustomTelemetryLogger logger;
    private CustomTelemetryLogger textOutputLogger;
    private CustomTelemetryLogger logger2;

    private boolean buttonPressed = false;



    @Override
    public void runOpMode() {
        initMotors();

        telemetry.addLine("Waiting for start");
        telemetry.update();
        double currentArmPosition = 0.65; // start position for armServo
        boolean buttonPressed = false;
        //variables for debug
        String moves = "";
        powerFactor = 0.6;
        double startposright = rf_drive.getCurrentPosition();
        double startposleft = lf_drive.getCurrentPosition();
        int numberlog = 0;
        boolean clawChanged = false;
        boolean debug = false;
        boolean depadPressed = false;
        boolean turn = false;
        int allowOtherMovement = 0;
        int preLogLiftPos = 0;
        int lastLogLiftPos = 0;
        debug = true;
        telemetry.addLine("debug on");
        telemetry.update();
        /*
        claw.setPosition(-1);
        telemetry.addLine(" off");
        telemetry.update();
        sleep(2000);
        claw.setPosition(1);
        telemetry.addLine(" on");
        telemetry.update();*/
        //wheelTest();
//        clawTest();
        boolean startedMoving = false;
        boolean alreadySwitchedMode = false;
        boolean minLiftAchieved = false;
        boolean maxLiftAchieved = false;
        int cachedLiftPos = 0;
        String lastlogged = "";
        String logFilePath = "";
        /*
         * Wait for the user to press start on the Driver Station
         */
        try {
            waitForStart();
            if (debug) {
                logFilePath = String.format("/sdcard/Logs/" + new Date().getHours() + "_" + new Date().getMinutes() + "_" + new Date().getSeconds() + ".txt", Environment.getExternalStorageDirectory().getAbsolutePath());
                String latestLogFilePath = "/sdcard/Logs/latest.txt";
                logger = new CustomTelemetryLogger(logFilePath);
                textOutputLogger = new CustomTelemetryLogger(AutoInstructionConstants.autoInstructPath);
                logger2 = new CustomTelemetryLogger(latestLogFilePath, false);
                telemetry.addData("name of file: ", logFilePath);
                telemetry.addData("name of auto instruct file: ", AutoInstructionConstants.autoInstructPath);
                telemetry.update();

            }


            while (opModeIsActive()) {

                if (debug) {

                    // Dpad up starts the logging pipeline
                    if (gamepad1.dpad_up) {
                        startposright = rf_drive.getCurrentPosition();
                        startposleft = lf_drive.getCurrentPosition();
                        telemetry.addLine("log start");
                        telemetry.update();
                    }
                    if ((!gamepad1.dpad_down && !gamepad1.dpad_right && !gamepad1.dpad_left && !gamepad1.a && !gamepad1.b) && depadPressed)
                        depadPressed = false;
                    if (gamepad1.a && !depadPressed) {
                        depadPressed = true;
                        lastlogged = "arm";
                        moves += " arm.setPosition(" + arm.getPosition() + ");\n";
                    }
                    if (gamepad1.b && !depadPressed) {
                        depadPressed = true;
                        lastlogged = "lift";
                        moves += "moveMotor(lift, " + lift.getCurrentPosition() + ", 0.5, true);\nsleep(250);\n";
                    }
                    // Dpad down starts logging the movement
                    if (gamepad1.dpad_down && !depadPressed) {
                        depadPressed = true;
                        numberlog++;
                        double rightDif = (rf_drive.getCurrentPosition() - startposright);
                        double leftDif = (lf_drive.getCurrentPosition() - startposleft);
                        logger.logData("log num: " + numberlog + "\n");
                        logger.logData("right movement:" + rightDif + "\n");
                        logger.logData("left movement:" + leftDif + "\n");

                        logger2.logData("log num: " + numberlog + "\n");
                        logger2.logData("right movement:" + rightDif + "\n");
                        logger2.logData("left movement:" + leftDif + "\n");
                        telemetry.addData("log end", numberlog);
                        telemetry.update();
                        boolean vertical = ((rightDif >= 0));
                        switch (allowOtherMovement) {
                            case 1:

                                telemetry.addData("vert", (leftDif < 0));
                                telemetry.update();
                                lastlogged = "vertical";
                                moves += "moveBot(" + ((Math.abs(leftDif)) / intCon/ticsPerInch) + "," + ((leftDif < 0) ? 1 : -1) + ", 0, 0);\nsleep(150);\n";
                                break;
                            case 2:
                                telemetry.addData("horz", (rightDif > 0));
                                telemetry.update();
                                lastlogged = "horizontal";
                                moves += "moveBot(" + ((Math.abs(rightDif)) / intCon/ticsPerInch) + ",0,0," + ((rightDif > 0) ? 1 : -1) + ");\nsleep(150);\n";
                                break;
                            case 3:
                                telemetry.addLine("turn");
                                telemetry.update();
                                lastlogged = "turn";
                                moves += ("turnBot(" + (((double) ((ticsToDegrees((int) (Math.round(leftDif))))))/degConv) + ");\nsleep(250);\n");
                                break;
                        }
                        allowOtherMovement = 0;

                        if ((gamepad2.right_trigger < 0.1 && gamepad2.left_trigger < 0.1 ) && clawChanged)
                            clawChanged = false;
                        if(gamepad2.right_trigger  > 0.1 && !clawChanged)
                        {
                            lastlogged = "claw open";
                            moves += "claw.setPosition(" + this.openClaw + ");\n";
                            clawChanged = true;
                        }
                        if(gamepad2.left_trigger > 0.1 && !clawChanged)
                        {
                            lastlogged = "claw closed";
                            moves += "claw.setPosition(" + this.closedClaw + ");\n";
                            clawChanged = true;
                        }


                       /* if (preLogArmPos != lastLogArmPos) {
                            moves += "arm.setPosition(" + arm.getPosition() + ");\nsleep(500);\n";
                        }*/

                        startposright = rf_drive.getCurrentPosition();
                        startposleft = lf_drive.getCurrentPosition();
                        turn = false;

                    }


                    // Dpad up actually sends the data to the FileWriter
                    if (gamepad1.dpad_right && !depadPressed) {
                        depadPressed = true;
                        logger.logData(moves);
                        textOutputLogger.logData("\n\n" + AutoInstructionCodeSerializer.serialize(moves) + "\n\n");
                        logger2.logData(moves);
                        telemetry.addData("logged moves: ", moves);
                        telemetry.update();
                        break;

                    }


                    if (gamepad2.right_stick_y != 0 || gamepad2.left_stick_y != 0)
                        buttonPressed = false;
                }
                if (gamepad2.left_stick_y < 0) {
                    currentArmPosition += this.manualArmSpeed; // increase by a small step
//                if(currentArmPosition > 1) currentArmPosition = 1;
               if (currentArmPosition >= this.maxArm) currentArmPosition = this.maxArm;
                } else if (gamepad2.left_stick_y > 0) {
                    currentArmPosition -= this.manualArmSpeed; // decrease by a small steps
                    if (currentArmPosition <= this.minArm) currentArmPosition = this.minArm;
                }
                moveServo(arm, currentArmPosition, 20);

                //   if (gamepad2.left_trigger > 0) {claw.setPosition(this.closedClawPos);  clawChanged = 1;} //grab claw
                //   if (gamepad2.right_trigger > 0) {claw.setPosition(this.openClawPos); clawChanged = 2;}//drop

                /* drivejava driving mechanisms here */

                telemetry.addData("name of file: ", logFilePath);
                telemetry.addData("# log:", numberlog);
                telemetry.addData("latest update: ", lastlogged);
                telemetry.addLine("=== Controls ===");

// Gamepad 1 Controls
                telemetry.addLine("Gamepad 1:");
                telemetry.addLine("- Left Stick Y: Forward/Backward Movement");
                telemetry.addLine("- Left Stick X: Sideways Movement");
                telemetry.addLine("- Right Stick X: Turning");
                telemetry.addLine("- D-Pad Up: Start Logging");
                telemetry.addLine("- D-Pad Down: Log Movement");
                telemetry.addLine("- D-Pad Right: Save Logged Moves");
                telemetry.addLine("- Button A: Log Arm Position");
                telemetry.addLine("- Button B: Log Lift Movement");

// Gamepad 2 Controls
                telemetry.addLine("Gamepad 2:");
                telemetry.addLine("- Left Stick Y: Move Arm");
                telemetry.addLine("- Right Stick Y: Control Lift");
                telemetry.addLine("- Left Trigger: Close Claw");
                telemetry.addLine("- Right Trigger: Open Claw");
                telemetry.addLine("press y on either controller to confirm which controller it is");
                if(gamepad1.y)
                {
                    telemetry.addLine("this is gamepad 1");
                }
                if(gamepad2.y)
                {
                    telemetry.addLine("this is gamepad 2");
                }
                telemetry.update();
                if (gamepad2.right_stick_y != 0) {
                    if (lift.getCurrentPosition() >= this.minLift && gamepad2.right_stick_y > 0 && startedMoving) {
                        telemetry.addLine("controlling min lift");
                        alreadySwitchedMode = false;
                        cachedLiftPos = this.minLift;
                        if (!minLiftAchieved) {
                            moveMotor(lift, this.minLift, this.liftStationaryPower, true);
                            minLiftAchieved = true;
                        }
                    } else if (lift.getCurrentPosition() <= this.maxLift && gamepad2.right_stick_y < 0 && startedMoving) {
                        alreadySwitchedMode = false;
                        cachedLiftPos = this.maxLift;
                        if (!maxLiftAchieved) {
                            moveMotor(lift, this.maxLift, this.liftStationaryPower, true);
                            maxLiftAchieved = true;
                        }
                    } else {
                        startedMoving = true;
                        minLiftAchieved = false;
                        maxLiftAchieved = false;
                        if (!alreadySwitchedMode) {
                            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                            alreadySwitchedMode = true;
                        }
                        lift.setPower(gamepad2.right_stick_y * 0.5);
                        cachedLiftPos = lift.getCurrentPosition();
                    }

                } else {
                    if (startedMoving) {
                        alreadySwitchedMode = false;
                        moveMotor(lift, cachedLiftPos, this.liftStationaryPower, true);
                    }
                }

                if (gamepad2.left_trigger > 0.1) {
                    //close
                    claw.setPosition(this.closedClaw);
                }
                // drop

                if (gamepad2.right_trigger > 0.1) {
                    //open
                    claw.setPosition(this.openClaw);
                }


                switch (allowOtherMovement) {
                    case 1:
                        moveBot(-gamepad1.left_stick_y, 0, 0);
                        break;
                    case 2:
                        moveBot(0, 0, gamepad1.left_stick_x);
                        break;
                    case 3:
                        moveBot(0, (gamepad1.right_stick_x), 0);
                        break;
                }
                if (gamepad1.left_stick_y != 0 && allowOtherMovement == 0) {
                    allowOtherMovement = 1;
                    telemetry.addLine("1");
                    telemetry.update();
                }
                if (gamepad1.left_stick_x != 0 && allowOtherMovement == 0) {
                    telemetry.addLine("2");
                    telemetry.update();
                    allowOtherMovement = 2;
                }
                if (gamepad1.right_stick_x != 0 && allowOtherMovement == 0) {
                    telemetry.addLine("3");
                    telemetry.update();
                    allowOtherMovement = 3;
                }
            }


        }catch (IOException e) {
            telemetry.addData("Error", "IOException: " + e.getMessage());
            telemetry.update();
        } finally {
            if (logger != null) {
                logger.close();
            }
            if (textOutputLogger != null) {
                textOutputLogger.close();
            }
            if (logger2 != null) {
                logger2.close();
            }
        }
        if (debug && logger != null && textOutputLogger != null) {
            /*
            logger.logData(moves);
            telemetry.addData("logged moves: ", moves);*/
            textOutputLogger.close();
            logger.close();
        }
        if (debug && logger2 != null) {
            logger2.close();
        }
        telemetry.addLine("logger close");
        telemetry.update();
    }
    private void moveBot(float vertical, float pivot, float horizontal) {
        pivot *= 0.5;
        rf_drive.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb_drive.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf_drive.setPower(powerFactor * (pivot + vertical + horizontal));
        lb_drive.setPower(powerFactor * (pivot + (vertical - horizontal)));

    }


}





