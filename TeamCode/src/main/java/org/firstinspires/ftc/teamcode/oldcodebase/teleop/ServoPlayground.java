package org.firstinspires.ftc.teamcode.oldcodebase.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.oldcodebase.PersistentTelemetry;
import org.firstinspires.ftc.teamcode.oldcodebase.RobotBase;

import java.util.ArrayList;
import java.util.HashMap;

@TeleOp(name = "ServoPlayground")
public class ServoPlayground extends RobotBase {

    @Override
    public void runOpMode() {
        this.initMotors();
        telemetry.addLine("Waiting for start");
        telemetry.addLine("Manual:");
        telemetry.addLine("Use dpad up and dpad down to cycle through digits and motors/servos");
        telemetry.addLine("Use dpad right to add new digits to the position");
        telemetry.addLine("Use dpad left to go to the next step");
        telemetry.addLine("Press a to execute the *newest* added position (that is, when each added position is executed, it is removed from the current queue and then the second to last added position is the newest position that it will navigate to)");
        telemetry.addLine("Press y to clear all positions");
        telemetry.update();

        waitForStart();

        final String[] servos = new String[] { "lift", "arm", "claw", "slidePower", "slidePos", "slideArm", "leftRoller", "rightRoller" };
        final char[] digitCycle = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '.' };

        ArrayList<HashMap<String, String>> positionsToNavigateTo = new ArrayList<>();
        StringBuffer currentPos = new StringBuffer("");

        PersistentTelemetry persistentTelemetry = new PersistentTelemetry(telemetry);

        int currServoIndex = 0;
        int digitCycleIndex = 0;
        int currentPosIndex = 0;

        boolean inputtingPos = false;
        boolean dpadPressed = false;
        boolean buttonPressed = false;
        boolean startedNewPos = false;
        while (opModeIsActive()) {

            if (startedNewPos) {
                persistentTelemetry.addLine("Current servo selected: " + servos[currServoIndex]);
                startedNewPos = false;
            } else {
                persistentTelemetry.setLine("Current servo selected", "Current servo selected: " + servos[currServoIndex] + "\n");
            }
            persistentTelemetry.update();



            if (gamepad1.dpad_up && !dpadPressed) {
                dpadPressed = true;
                if (inputtingPos) {

                    digitCycleIndex++;
                    if (digitCycleIndex >= digitCycle.length) {
                        digitCycleIndex = 0;
                    }
                    currentPos.setCharAt(currentPosIndex, digitCycle[digitCycleIndex]);
                    persistentTelemetry.setLine("Current servo pos", "Current servo pos: " + currentPos);
                    persistentTelemetry.update();

                } else {
                    currServoIndex--;
                    if (currServoIndex <= 0) currServoIndex = 0;
                    persistentTelemetry.setLine("Current servo selected", "Current servo selected: " + servos[currServoIndex] + "\n");
                    persistentTelemetry.update();
                }
            }

            if (gamepad1.dpad_down && !dpadPressed) {

                dpadPressed = true;
                if (inputtingPos) {

                    digitCycleIndex--;
                    if (digitCycleIndex < 0) {
                        digitCycleIndex = digitCycle.length - 1;
                    }
                    currentPos.setCharAt(currentPosIndex, digitCycle[digitCycleIndex]);
                    persistentTelemetry.setLine("Current servo pos", "Current servo pos: " + currentPos);
                    persistentTelemetry.update();

                } else {
                    currServoIndex++;
                    if (currServoIndex >= servos.length) currServoIndex = 0;
                    persistentTelemetry.setLine("Current servo selected", "Current servo selected: " + servos[currServoIndex] + "\n");
                    persistentTelemetry.update();
                }

            }

            if (gamepad1.dpad_left && !dpadPressed) {

                dpadPressed = true;
                if (!inputtingPos) {
                    inputtingPos = true;

                    currentPos.ensureCapacity(currentPosIndex + 1);
                    currentPos.setLength(currentPosIndex + 1);
                    currentPos.setCharAt(currentPosIndex, digitCycle[digitCycleIndex]);

                    persistentTelemetry.addLine("Inputting servo pos");
                    persistentTelemetry.addLine("Current servo pos: " + currentPos);
                    persistentTelemetry.update();
                } else {
                    inputtingPos = false;
                    persistentTelemetry.addLine("Done inputting servo pos");
                    persistentTelemetry.update();

                    HashMap<String, String> currServoMap = new HashMap<>();
                    currServoMap.put(servos[currServoIndex], currentPos.toString());
                    positionsToNavigateTo.add(currServoMap);

                    persistentTelemetry.addLine("Added servo pos to list of servo positions to navigate to");
                    persistentTelemetry.update();

                    currentPos = new StringBuffer("");
                    digitCycleIndex = 0;
                    currServoIndex = 0;
                    currentPosIndex = 0;
                    startedNewPos = true;
                }

            }

            if (gamepad1.dpad_right && !dpadPressed && inputtingPos) {

                dpadPressed = true;
                currentPosIndex++;
                currentPos.ensureCapacity(currentPosIndex + 1);
                currentPos.setLength(currentPosIndex + 1);
                digitCycleIndex = 0;
                currentPos.setCharAt(currentPosIndex, digitCycle[digitCycleIndex]);

            }

            if ((!gamepad1.dpad_down && !gamepad1.dpad_right && !gamepad1.dpad_left && !gamepad1.dpad_up) && dpadPressed) {
                dpadPressed = false;
            }
            if ((!gamepad1.a && !gamepad1.y) && buttonPressed) {
                buttonPressed = false;
            }
            if (inputtingPos) {
                persistentTelemetry.setLine("Current servo pos", "Current servo pos: " + currentPos);
                persistentTelemetry.update();
            }


            if (gamepad1.a && !buttonPressed) {
                buttonPressed = true;

                if (positionsToNavigateTo.isEmpty()) {
                    persistentTelemetry.addLine("No target positions to navigate to");
                    persistentTelemetry.update();
                } else {

                    HashMap<String, String> posToNavigateTo = positionsToNavigateTo.get(0);
                    String servoName = new ArrayList<>(posToNavigateTo.keySet()).get(0);
                    String rawServoPos = posToNavigateTo.get(servoName);
                    double servoPos = Double.parseDouble(rawServoPos);

                    persistentTelemetry.addLine("Moving servo " + servoName + " to pos " + rawServoPos);
                    persistentTelemetry.update();
                    switch (servoName) {
                        case "lift":
                            moveMotor(lift, Integer.parseInt(rawServoPos.split("\\.")[0]), 0.35, true);
                            break;
                        case "arm":
//                            moveServo(arm, Double.parseDouble(rawServoPos), 20);
                            arm.setPosition(servoPos);
                            break;
                        case "claw":
//                            moveServo(claw, Double.parseDouble(rawServoPos), 20);
                            claw.setPosition(servoPos);
                            break;
                        case "slidePower":
                            slide1.setPower(servoPos);
                            slide2.setPower(servoPos);
                            sleep(1000);
                            slide1.setPower(0);
                            slide2.setPower(0);
                            telemetry.addLine("Pos eventual: " + slide1.getCurrentPosition());
                            break;
                        case "slidePos":
                            runTasksAsync(
                                    () -> moveMotor(slide1, (int) servoPos, 0.4),
                                    () -> slide2.setPower(0.4)
                            );
                            break;
                        case "slideArm":
                            slideArm.setPosition(servoPos);
                            break;
                        case "leftRoller":
//                            leftWheel.setPosition(servoPos);
                            leftRoller.setPower(servoPos);
                            sleep(3000);
                            leftRoller.setPower(0);
                            break;
                        case "rightRoller":
//                            rightWheel.setPosition(servoPos);
                            rightRoller.setPower(servoPos);
                            sleep(3000);
                            rightRoller.setPower(0);
                            break;
                    }
                    persistentTelemetry.addLine("Done moving");
                    persistentTelemetry.update();
                    positionsToNavigateTo.remove(0);

                }

            }
            if (gamepad1.y && !buttonPressed) {
                buttonPressed = true;
                positionsToNavigateTo.clear();
                persistentTelemetry.clear();
                persistentTelemetry.update();
                currentPos = new StringBuffer("");
                currServoIndex = 0;
                digitCycleIndex = 0;
                currentPosIndex = 0;

                inputtingPos = false;
                dpadPressed = false;
            }



        }
    }
}
