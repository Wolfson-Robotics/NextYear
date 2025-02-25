package org.firstinspires.ftc.teamcode.oldcodebase.auto;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.oldcodebase.PersistentTelemetry;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "AutoJavaExperimentalMovements")
public class AutoJavaExperimentalMovements extends AutoJava {

    private final double powerFactor = 0.4;
    private final PersistentTelemetry pTelem = new PersistentTelemetry(this.telemetry);

    private double startRf = 0;
    private double startLf = 0;

    private double lowestJunk = Double.POSITIVE_INFINITY;
    private double highestJunk = Double.NEGATIVE_INFINITY;
    private final double maxDist = 20;

    public AutoJavaExperimentalMovements() {
        super(true);
    }

    // down by -3 and left by -3
    public void trial1() {
        beginTrial();
        print("trial 1");
        rb_drive.setPower(powerFactor * -1);
        rf_drive.setPower(powerFactor * -1);
        endTrial();
    }
    // down by -1 and left by -1
    public void trial2() {
        beginTrial();
        print("trial 2");
        rb_drive.setPower(powerFactor * -0.33333333333333333333333333333333333333333);
        lf_drive.setPower(powerFactor * -0.3333333333333333333333333333333333333333333);
        endTrial();
    }
    // up by 2 and right by 1
    public void trial3() {
        beginTrial();
        print("trial 3");
        rf_drive.setPower(powerFactor * 0.333333333333333333333333333333333333);
        rb_drive.setPower(powerFactor * 1);
        lf_drive.setPower(powerFactor * 1);
        lb_drive.setPower(powerFactor * 0.333333333333333333333333333333333333333);
        endTrial();
    }
    // up by 3 and right by 1
    public void trial4() {
        beginTrial();
        print("trial 4");
        rf_drive.setPower(powerFactor * 0.2);
        rb_drive.setPower(powerFactor * 0.8);
        lf_drive.setPower(powerFactor * 0.8);
        lb_drive.setPower(powerFactor * 0.2);
        endTrial();
    }
    // up by 1 and right by 2
    public void trial5() {
        beginTrial();
        print("trial 5");
        rf_drive.setPower(powerFactor * -0.33333333333333333333333);
        rb_drive.setPower(powerFactor * 1);
        lf_drive.setPower(powerFactor * 1);
        lb_drive.setPower(powerFactor * -0.333333333333333333333333);
        endTrial();
    }


    public void trial6() {
        beginTrial();
        print("trial 6");
        moveBotOld(15, 1, 0, 1);
        endTrial();
    }
    public void trial7() {
        beginTrial();
        print("trial 7");
        moveBotOld(15, 0, 1, 0);
        endTrial();
    }
    public void trial8() {
        beginTrial();
        print("trial 8");
        moveBotOld(15, 1, 1, 1);
        endTrial();
    }

    // lf experiment
    public void turnTrial1() {
        beginTrial();
        print("turn trial 1");
        print("changing powers attempting to turn (1)");
        double runCounter = 40;
        double lastPower1 = 1;
        double lastPower2 = 1;
        rf_drive.setPower(powerFactor * lastPower1);
        rb_drive.setPower(powerFactor * lastPower2);
        lf_drive.setPower(powerFactor * lastPower1);
        lb_drive.setPower(powerFactor * lastPower2);
        sleep(1000);
        while (runCounter > 0) {
            rf_drive.setPower(powerFactor * lastPower1);
            rb_drive.setPower(powerFactor * lastPower2);
            lf_drive.setPower(powerFactor * lastPower1);
            lb_drive.setPower(powerFactor * lastPower2);
            ElapsedTime runtime = new ElapsedTime();
            runtime.reset();
            if (runtime.milliseconds() > 100) {
                lastPower1 -= 0.1;
                lastPower2 += 0.05;
                runCounter--;
            }
        }
        endTrial();
    }

    public void turnTrial2() {
        beginTrial();
        print("turn trial 2");
        print("changing powers attempting to turn (2)");
        double runCounter = 40;
        double lastPower1 = -1;
        double lastPower2 = -1;
        rf_drive.setPower(powerFactor * lastPower1);
        rb_drive.setPower(powerFactor * lastPower2);
        lf_drive.setPower(powerFactor * lastPower1);
        lb_drive.setPower(powerFactor * lastPower2);
        sleep(1000);
        while (runCounter > 0) {
            rf_drive.setPower(powerFactor * lastPower1);
            rb_drive.setPower(powerFactor * lastPower2);
            lf_drive.setPower(powerFactor * lastPower1);
            lb_drive.setPower(powerFactor * lastPower2);
            ElapsedTime runtime = new ElapsedTime();
            runtime.reset();
            if (runtime.milliseconds() > 100) {
                lastPower1 -= 0.05;
                lastPower2 += 0.05;
                runCounter--;
            }
        }
        endTrial();
    }


    public void miscTrial1() {
        beginTrial();
        print("misc trial 1");
        print("smooth braking");
        double runCounter = 15;
        double lastPower1 = 1;
        double lastPower2 = 1;
        rf_drive.setPower(powerFactor * lastPower1);
        rb_drive.setPower(powerFactor * lastPower2);
        lf_drive.setPower(powerFactor * lastPower1);
        lb_drive.setPower(powerFactor * lastPower2);
        sleep(1000);
        while (runCounter > 0) {
            rf_drive.setPower(powerFactor * lastPower1);
            rb_drive.setPower(powerFactor * lastPower2);
            lf_drive.setPower(powerFactor * lastPower1);
            lb_drive.setPower(powerFactor * lastPower2);
            ElapsedTime runtime = new ElapsedTime();
            runtime.reset();
            if (runtime.milliseconds() > 100) {
                lastPower1 -= 0.065;
                lastPower2 -= 0.065;
                runCounter--;
            }
        }
    }
    public void miscTrial2() {
        runTasksAsync(
                () -> turnBot(180),
                () -> moveMotor(lift, -3000, 1)
        );
    }

    public void miscTrial3() {
//        moveBotDiag(10, 10, 1, 1);
//        moveBotDiag(10, 40, 1, 1);
//        moveBotDiag(25.23172628304821, 35.0108864696734, 1, 1);
        moveBotDiag(35.0108864696734, 25.23172628304821, 1, 1);
        sleep(300);
    }
    public void miscTrial4() {
        moveBotSmooth(30, 1, 0, 0);
        sleep(300);
    }




    private void endTrial() {
        sleep(3000);
        removePower();
        print("change in pos rf: " + (rf_drive.getCurrentPosition() - startRf));
        print("change in pos lf: " + (lf_drive.getCurrentPosition() - startLf));
        sleep(1000);
    }
    private void beginTrial() {
        startRf = rf_drive.getCurrentPosition();
        startLf = lf_drive.getCurrentPosition();
    }


    @Override
    public void runOpMode() {
        this.commonAutoInit();

//        telemetry.addLine(String.valueOf(((VoltageSensor) hardwareMap.voltageSensor).getVoltage()));
//        telemetry.update();


        boolean runningTrials = false;
        while (opModeIsActive()) {

            if (distanceSensor != null) {
                pTelem.setData("distance", distanceSensor.getDistance(DistanceUnit.INCH));
                pTelem.update();
            }
/*
            if (colorSensor != null) {
                int red = colorSensor.red();
                int green = colorSensor.green();
                int blue = colorSensor.blue();
                pTelem.setData("alpha", colorSensor.alpha());
                pTelem.setData("red raw light", red);
                pTelem.setData("green raw light", green);
                pTelem.setData("blue raw light", blue);
                int max = Math.max(red, Math.max(green, blue));
                double normred = (double)red/max;
                double normgreen = (double)green/max;
                double normblue = (double)blue/max;
                pTelem.setData("red normalized", normred);
                pTelem.setData("green normalized", normgreen);
                pTelem.setData("blue normalized", normblue);
                double redFactor = (double) colorSensor.red() / (double) colorSensor.blue();
                double greenFactor = (double) colorSensor.green() / (double) colorSensor.blue();
                if (normgreen >= 0.8 && normred >= 0.7 && normblue < 0.7 && colorSensor.getRawLightDetected() > 75) {
                    pTelem.setLine("sees", "sees yellow");
                } else {
                    pTelem.setLine("sees", "sees nothing");
                }

                pTelem.setData("raw light true", colorSensor.getRawLightDetected());
                pTelem.setData("raw light max", colorSensor.getRawLightDetectedMax());
                pTelem.setData("raw light optical", colorSensor.rawOptical());
                pTelem.setData("distance in inches", colorSensor.getDistance(DistanceUnit.INCH));
                pTelem.setData("raw rgb", colorSensor.argb());

                int raw = colorSensor.argb();
                pTelem.setLine("rgb deconstructed", "rgb deconstructed: " + Color.red(raw) + ", " + Color.green(raw) + ", " + Color.blue(raw));
                pTelem.update();
            }*/


            if (gamepad1.left_stick_x > 0.5 && gamepad1.right_stick_x < -0.5 && !runningTrials) {
                runningTrials = true;
                trial1();
                trial2();
                trial3();
                trial4();
                trial5();
            }
            if (gamepad1.left_stick_x < -0.5 && gamepad1.right_stick_x > 0.5 && !runningTrials) {
                runningTrials = true;
                turnTrial1();
            }
            if (gamepad1.left_stick_y > 0.5 && gamepad1.right_stick_y > 0.5 && !runningTrials) {
                runningTrials = true;
                miscTrial1();
            }
            if (gamepad1.left_stick_y < -0.5 && gamepad1.right_stick_y < -0.5 && !runningTrials) {
                print("testing moveebot in all cardinal directions");
                moveBotOld(10, 0, 0, -1);
                sleep(1000);
                moveBotOld(10, 0, 0, 1);
                sleep(1000);
                moveBotOld(10, -1, 0, 0);
                sleep(1000);
                moveBotOld(10, 1, 0, 0);
                sleep(1000);
                print("testing turnbot in all cardinal directions");
                turnBot(90);
                sleep(1000);
                turnBot(180);
                sleep(1000);
                turnBot(-90);
                sleep(1000);
            }
            runningTrials = false;

            if (gamepad1.dpad_up) {
                trial1();
            }
            if (gamepad1.dpad_right) {
//                movetillyellow(false);
                moveTillObjectSeen(true, false);
                sleep(5000);
            }
            if (gamepad1.dpad_down) {
                trial3();
            }
            if (gamepad1.dpad_left) {
//                movetillyellow(true);
                moveTillObjectSeen(false, false);
                sleep(5000);
            }
            if (gamepad1.y) {
                trial5();
            }
            if (gamepad1.b) {
                trial6();
            }
            if (gamepad1.a) {
                trial7();
            }
            if (gamepad1.x) {
                trial8();
            }
            // diagonal
            if (gamepad1.left_bumper) {
                miscTrial3();
            }
            // smooth
            if (gamepad1.right_bumper) {
                miscTrial4();
            }

            if (gamepad1.left_trigger > 0.1) {
                double currDist = distanceSensor.getDistance(DistanceUnit.INCH);
                if (currDist < maxDist) {
                    if (currDist < lowestJunk) {
                        lowestJunk = currDist;
                    } else if (currDist > highestJunk) {
                        highestJunk = currDist;
                    }
                }
                pTelem.setLine("Calibra", "Calibrating");
                pTelem.setData("Current lowest junk", lowestJunk);
                pTelem.setData("Current highest junk", highestJunk);
                pTelem.update();
            } else {
                pTelem.removeLine("Calibra");
                pTelem.removeLine("Current lowest junk");
                pTelem.removeLine("Current highest junk");
                pTelem.update();
            }
            /*
            if (gamepad1.a) {
                trial5();
            }*//*
            if (gamepad1.y) {
                miscTrial2();
            }*/

        }
    }


    /*
     private void movetillyellow(boolean left)
    {
        int red = colorSensor.red();
        int green = colorSensor.green();
        int blue = colorSensor.blue();
        int max = Math.max(red, Math.max(green, blue));
        double normred = (double)red/max;
        double normgreen = (double)green/max;
        double normblue = (double)blue/max;
        double horizontal = (left) ? -0.45 : 0.45;
        boolean yellow = false;
        rf_drive.setPower(-horizontal);
        rb_drive.setPower(horizontal);
        lf_drive.setPower(horizontal);
        lb_drive.setPower(-horizontal);
        while(!yellow && opModeIsActive()) {
            red = colorSensor.red();
            green = colorSensor.green();
            blue = colorSensor.blue();
            telemetry.addData("alpha1", colorSensor.alpha());
            telemetry.addData("red raw light1", red);
            telemetry.addData("green raw light1", green);
            telemetry.addData("blue raw light1", blue);
            max = Math.max(red, Math.max(green, blue));
            normred = (double) red / max;
            normgreen = (double) green / max;
            normblue = (double) blue / max;
            double redFactor = (double) colorSensor.red() / (double) colorSensor.blue();
            double greenFactor = (double) colorSensor.green() / (double) colorSensor.blue();
            telemetry.addData("red normalized1", normred);
            telemetry.addData("green normalized1", normgreen);
            telemetry.addData("blue normalized1", normblue);
//            if (normgreen >= 0.8 && normred >= 0.7 && normblue < 0.7 && colorSensor.getRawLightDetected() > 75) {
            if (redFactor > 1.6 && greenFactor > 1.6 && colorSensor.getRawLightDetected() > 80) {
                yellow = true;
            }
                telemetry.update();
            }
        telemetry.addLine("done");
        telemetry.update();
        removePower();
        sleep(300);
        claw.setPosition(openClaw);
        sleep(400);
        grabSample();





    }*/

/*

    public void moveTillObjectSeen(boolean left) {
//        double lowestJunk = 6d, highestJunk = 9.6d;
//        double lowestJunk = 9.5, highestJunk = 13.75;
        if (lowestJunk == Double.NEGATIVE_INFINITY) {
            lowestJunk = 9.5;
            pTelem.addLine("Did not calibrate, going to default values");
            sleep(2500);
        }
        if (highestJunk == Double.POSITIVE_INFINITY) {
            highestJunk = 13.75;
            pTelem.addLine("Did not calibrate, going to default values");
            sleep(2500);
        }
        pTelem.addLine("Using lowestJunk as " + lowestJunk);
        pTelem.addLine("Using highestJunk as " + highestJunk);
        pTelem.update();

        double distSeen = distanceSensor.getDistance(DistanceUnit.INCH);
        pTelem.setData("distance", distSeen);
        pTelem.update();

        double horizontal = (left) ? -0.45 : 0.45;
        rf_drive.setPower(-horizontal);
        rb_drive.setPower(horizontal);
        lf_drive.setPower(horizontal);
        lb_drive.setPower(-horizontal);

        boolean found = false;
        while (!found && opModeIsActive()) {
            distSeen = distanceSensor.getDistance(DistanceUnit.INCH);
            if (distSeen < lowestJunk || (distSeen > highestJunk && distSeen < maxDist)) {
                removePower();
                List<Double> distancesWhile = new ArrayList<>();
                runTasksAsync(
                        () -> sleep(200),
                        () -> distancesWhile.add(distanceSensor.getDistance(DistanceUnit.INCH))
                );
                if (Math.abs(distancesWhile.stream().mapToDouble(Double::doubleValue).average().orElseThrow(() -> new IllegalArgumentException("a")) - distSeen) < 0.8) {
                    pTelem.addLine("Confirmed object location");
                    pTelem.update();
                    break;
                }
            }
            rf_drive.setPower(-horizontal);
            rb_drive.setPower(horizontal);
            lf_drive.setPower(horizontal);
            lb_drive.setPower(-horizontal);
        }
        pTelem.addLine("Done");
        pTelem.update();
        removePower();
        sleep(300);
        claw.setPosition(openClaw);
        sleep(400);
        grabSample();
    }*/








    private void print(String message) {
//        telemetry.addLine(message);
//        telemetry.update();
        pTelem.addLine(message);
        pTelem.update();
    }

    private void moveBot(float vertical, float pivot, float horizontal) {
        pivot *= 0.6;
        rf_drive.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb_drive.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf_drive.setPower(powerFactor * (pivot + vertical + horizontal));
        lb_drive.setPower(powerFactor * (pivot + (vertical - horizontal)));
    }
}
