package org.firstinspires.ftc.teamcode.oldcodebase.auto;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.oldcodebase.PersistentTelemetry;
import org.firstinspires.ftc.teamcode.oldcodebase.RobotBase;
import org.firstinspires.ftc.teamcode.oldcodebase.old.PixelDetection;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AutoJava extends RobotBase {


    protected ArrayList<String> movements = new ArrayList<>();

    protected PixelDetection pixelDetection;
    protected boolean blue = false;

    // Distance sensor constants
    protected double lowestJunk = 330;
    protected double highestJunk = -1;
    protected final double defaultLowestJunk = 9.5;
    protected final double defaultHighestJunk = 13.75;
    protected final double noLowestJunk = lowestJunk;
    protected final double noHighestJunk = highestJunk;
    // side note: the distance sensor returns 322 when it sees nothing
    protected final double maxDist = 10;
    protected final double maxDistDeviance = 0.445;


    protected AutoJava(boolean blue) {
        this.blue = blue;
    }

    @Override
    public void initMotors() {
        super.initMotors();
        this.setBrakeMotors();
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        powerFactor = 0.6;
        powerFactor = 0.725;
        arm.setPosition(this.minArm);
        claw.setPosition(this.closedClaw);
    }


    // pos vertical is forward, neg vertical is negative
    // pos pivot is clockwise, neg pivot is counterclockwise
    // pos horizontal is right, neg horizontal is left
    // notes:
    // rf_drive, when going forward, goes into the negative direction positionally
    protected void moveBotOld(double distIN, double vertical, double pivot, double horizontal) {

        // 23 motor tics = 1 IN
        int motorTics;
        int posNeg = (vertical >= 0) ? 1 : -1;

        rf_drive.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb_drive.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf_drive.setPower(powerFactor * (pivot + vertical + horizontal));
        lb_drive.setPower(powerFactor * (pivot + (vertical - horizontal)));

        if (horizontal != 0) {
            posNeg = (horizontal > 0) ? 1 : -1;
            motorTics = lf_drive.getCurrentPosition() + (int) ((distIN * intCon) * (posNeg));
            if (posNeg == 1) {
                // right goes negative
                while ((lf_drive.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    idle();
                }
            } else {
                // left goes positive
                while ((lf_drive.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    idle();
                }
            }
        } else {
            posNeg = vertical >= 0 ? -1 : 1;
            motorTics = rf_drive.getCurrentPosition() + (int) ((distIN * intCon) * posNeg);
            if (posNeg == -1) {
                while (rf_drive.getCurrentPosition() > motorTics && opModeIsActive()) {
                    idle();
                }
            } else {
                while ((rf_drive.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    idle();
                }
            }

        }
        removePower();

    }


    // 12 inches (or a foot) is 73.6770894730908 robot inches
    protected void moveBot(double in, double vertical, double pivot, double horizontal) {
//        moveBotOld((in/12d) * 73.6770894730908, vertical, pivot, horizontal);
        moveBotOld(in*ticsPerInch, vertical, pivot, horizontal);
    }

    protected double ticsToInches(double tics) {
        return tics*ticsPerInch*intCon;
    }






    protected void moveBotSmooth(double distIN, double vertical, double pivot, double horizontal) {
        // 23 motor tics = 1 IN
        int posNeg = (vertical >= 0) ? 1 : -1;

        TreeMap<Integer, Double> distPowerFactor = new TreeMap<>();
        distPowerFactor.put(900, 0.75);
        distPowerFactor.put(525, 0.5);
        distPowerFactor.put(210, 0.25);
        distPowerFactor.put(100, 0.1);

        List<Integer> traveledRanges = new ArrayList<>();


        rf_drive.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb_drive.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf_drive.setPower(powerFactor * (pivot + vertical + horizontal));
        lb_drive.setPower(powerFactor * (pivot + (vertical - horizontal)));

        DcMotorEx targetMotor;
        if (horizontal != 0) {
            posNeg = (horizontal > 0) ? 1 : -1;
            targetMotor = lf_drive;
        } else {
            posNeg = vertical >= 0 ? -1 : 1;
            targetMotor = rf_drive;
        }

        int motorTics = targetMotor.getCurrentPosition() + (int) ((distIN * intCon * ticsPerInch) * posNeg);
        Supplier<Boolean> distWait = posNeg == 1 ? () -> targetMotor.getCurrentPosition() < motorTics && opModeIsActive() : () -> targetMotor.getCurrentPosition() > motorTics && opModeIsActive();

        while (distWait.get()) {
            for (Map.Entry<Integer, Double> powerRange : distPowerFactor.entrySet()) {
                if (Math.abs(targetMotor.getCurrentPosition() - motorTics) <= powerRange.getKey() && !traveledRanges.contains(powerRange.getKey())) {
                    double pF = powerRange.getValue();
                    traveledRanges.add(powerRange.getKey());
                    rf_drive.setPower(rf_drive.getPower() * pF);
                    rb_drive.setPower(rb_drive.getPower() * pF);
                    lf_drive.setPower(lf_drive.getPower() * pF);
                    lb_drive.setPower(lb_drive.getPower() * pF);
                    break;
                }
            }
        }
        removePower();
    }





    protected void moveBotDiag(double horizIn, double vertIn, double vertical, double horizontal) {

        rf_drive.setPower(powerFactor * ((vertical - horizontal)));
        rb_drive.setPower(powerFactor * (vertical + horizontal));
        lf_drive.setPower(powerFactor * (vertical + horizontal));
        lb_drive.setPower(powerFactor * ((vertical - horizontal)));

        int lastVertPos = rf_drive.getCurrentPosition();
        int lastHorizPos = lf_drive.getCurrentPosition();
        AtomicInteger changeVert = new AtomicInteger(0);
        AtomicInteger changeHoriz = new AtomicInteger(0);
        AtomicInteger afterDiagVert = new AtomicInteger(0);
        AtomicInteger afterDiagHoriz = new AtomicInteger(0);

        int vertNeg = vertical >= 0 ? -1 : 1;
        int vertTics = rf_drive.getCurrentPosition() + (int) ((vertIn * intCon * ticsPerInch) * vertNeg);
        int horizNeg = (horizontal > 0) ? 1 : -1;
        int horizTics = lf_drive.getCurrentPosition() + (int) ((horizIn * intCon * ticsPerInch) * horizNeg);

        Supplier<Boolean> vertWait = vertNeg == 1 ? () -> rf_drive.getCurrentPosition() < vertTics && opModeIsActive() : () -> rf_drive.getCurrentPosition() > vertTics && opModeIsActive();
        Supplier<Boolean> horizWait = horizNeg == 1 ? () -> lf_drive.getCurrentPosition() < horizTics && opModeIsActive() : () -> lf_drive.getCurrentPosition() > horizTics && opModeIsActive();

        AtomicReference<Boolean> vertDone = new AtomicReference<>(false), horizDone = new AtomicReference<>(false);
        runTasksAsync(
                () -> {
                    while (vertWait.get() && opModeIsActive()) {
                        if (horizDone.get()) {
                            if (changeVert.get() == 0) {
                                changeVert.set(Math.abs(rf_drive.getCurrentPosition()) - Math.abs(lastVertPos));
                            }
                            if (afterDiagVert.get() == 0) {
                                afterDiagVert.set(rf_drive.getCurrentPosition());
                            }
                            if (afterDiagHoriz.get() == 0) {
                                afterDiagHoriz.set(lf_drive.getCurrentPosition());
                            }
                            rf_drive.setPower(powerFactor * vertical);
                            rb_drive.setPower(powerFactor * vertical);
                            lf_drive.setPower(powerFactor * vertical);
                            lb_drive.setPower(powerFactor * vertical);
                        }
                    }
                    if (changeVert.get() == 0) {
                        changeVert.set(Math.abs(rf_drive.getCurrentPosition()) - Math.abs(lastVertPos));
                    }
                    if (afterDiagVert.get() == 0) {
                        afterDiagVert.set(rf_drive.getCurrentPosition());
                    }
                    if (afterDiagHoriz.get() == 0) {
                        afterDiagHoriz.set(lf_drive.getCurrentPosition());
                    }
                    vertDone.set(true);
                },
                () -> {
                    while (horizWait.get() && opModeIsActive()) {
                        if (vertDone.get()) {
                            if (changeHoriz.get() == 0) {
                                changeHoriz.set(Math.abs(lf_drive.getCurrentPosition()) - Math.abs(lastHorizPos));
                            }
                            if (afterDiagVert.get() == 0) {
                                afterDiagVert.set(rf_drive.getCurrentPosition());
                            }
                            if (afterDiagHoriz.get() == 0) {
                                afterDiagHoriz.set(lf_drive.getCurrentPosition());
                            }
                            rf_drive.setPower(powerFactor * -horizontal);
                            rb_drive.setPower(powerFactor * horizontal);
                            lf_drive.setPower(powerFactor * horizontal);
                            lb_drive.setPower(powerFactor * -horizontal);
                        }
                    }
                    if (changeHoriz.get() == 0) {
                        changeHoriz.set(Math.abs(lf_drive.getCurrentPosition()) - Math.abs(lastHorizPos));
                    }
                    if (afterDiagVert.get() == 0) {
                        afterDiagVert.set(rf_drive.getCurrentPosition());
                    }
                    if (afterDiagHoriz.get() == 0) {
                        afterDiagHoriz.set(lf_drive.getCurrentPosition());
                    }
                    horizDone.set(true);
                },
                () -> {
                    while ((!horizDone.get() || !vertDone.get()) && opModeIsActive()) idle();
                    removePower();
                    telemetry.addData("rf", changeVert.get());
                    telemetry.addData("rf current", rf_drive.getCurrentPosition());
                    telemetry.addData("lf", changeHoriz.get());
                    telemetry.addData("lf current", lf_drive.getCurrentPosition());
                    telemetry.addData("rf past", lastVertPos);
                    telemetry.addData("lf past", lastHorizPos);
                    telemetry.update();
                    sleep(60000);
                }
        );
    }


    protected void moveBotTics(double motorTics, double vertical, double pivot, double horizontal) {

        int posNeg = (vertical >= 0) ? 1 : -1;

        rf_drive.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb_drive.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf_drive.setPower(powerFactor * (pivot + vertical + horizontal));
        lb_drive.setPower(powerFactor * (pivot + (vertical - horizontal)));

        if (horizontal >= 0) {
            motorTics = lf_drive.getCurrentPosition() + (int) ((motorTics) * posNeg);
            if (posNeg == -1) {
                while ((lf_drive.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    idle();
                }
            } else {
                while ((lf_drive.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    idle();
                }
            }
        } else {
            motorTics = rf_drive.getCurrentPosition() + (int) ((motorTics) * posNeg);
            while ((rf_drive.getCurrentPosition() < motorTics) && opModeIsActive()) {
                idle();
            }
        }
        removePower();

    }

    protected void turnBot(double degrees) {
        // 13.62 inches is default robot length
        double robotLength = 13.62;
        double distUnit = (robotLength) / (Math.cos(45));
        double distIN = (Math.abs((distUnit * ((degrees*1.75))) / 90))*degConv;
        int motorTics;
        int pivot = (degrees >= 0) ? 1 : -1;
        rf_drive.setPower(powerFactor * (-pivot));
        rb_drive.setPower(powerFactor * (-pivot));
        lf_drive.setPower(powerFactor * (pivot));
        lb_drive.setPower(powerFactor * (pivot));
        motorTics = lf_drive.getCurrentPosition() + (int) Math.round((distIN * intCon)* pivot);
        if (pivot == 1) {
            while ((lf_drive.getCurrentPosition() < motorTics) && opModeIsActive()) {
                idle();
            }
        }
        if (pivot == -1) {
            while ((lf_drive.getCurrentPosition() > motorTics) && opModeIsActive()) {
                idle();
            }
        }
        removePower();

    }


    public void liftBot(int level, double speed) {
        int liftPos = 0;
        switch (level)
        {
            case -1: {
                liftPos = -100;
                break;
            }
            case 0: {
                liftPos = -940;
                break;
            }
            case 1: {
//                liftPos = -940;
                liftPos = -1045;
                break;
            }
            case 2: {
                liftPos = -1350;
                break;
            }
            /*
            case -1: {
                liftPos = -100;
                break;
            }
            case 0: {
                liftPos = -800;
                break;
            }
            case 1: {
                liftPos = -1500;
                break;
            }
            case 2: {
                liftPos = -2650;
                break;
            }
            case 3: {
                liftPos = -4150;
                break;
            }*/
        }

        if (level == -1)
            lift.setDirection(DcMotorSimple.Direction.REVERSE);
        else if (level == 1) // positions change -1,2,1 when 2,1 happens direction should change
            lift.setDirection(DcMotorSimple.Direction.FORWARD);

        moveMotor(lift, liftPos, speed, true);
    }

    protected void liftBot(int level) {
        liftBot(level, 0.2975);
    }




    protected void commonAutoInit() {
        this.initMotors();
        /*
        colorSensor = hardwareMap.get(RevColorSensorV3.class, "colorsensor");
        if (colorSensor != null) {
            colorSensor.initialize();
        }
        */
        distanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distancesensor");
        if (distanceSensor != null) {
            distanceSensor.initialize();
        }
//        this.initCamera();
/*
        while (!isStarted()) {
            if ((gamepad1.right_trigger > 0.1 || gamepad2.right_trigger > 0.1)  && distanceSensor != null) {
                telemetry.addLine("Calibrating");
                double currDist = distanceSensor.getDistance(DistanceUnit.INCH);
//                if (currDist < maxDist) {
                    if (currDist < lowestJunk) {
                        lowestJunk = currDist;
                    } else if (currDist > highestJunk) {
                        highestJunk = currDist;
                    }
//                }
            }
            telemetry.addLine("Curr dist seen: " + distanceSensor.getDistance(DistanceUnit.INCH));
            telemetry.addLine("Current lowest junk: " + lowestJunk);
            telemetry.addLine("Current highest junk: " + highestJunk);
            telemetry.update();
        }
        lowestJunk -= 0.5;
        highestJunk += 0.5;*/
        try {
            StringBuilder content = new StringBuilder();
            try (FileReader reader = new FileReader("/sdcard/Logs/lower_calibrate.txt")) {
                int ch;
                while ((ch = reader.read()) != -1) {
                    content.append((char) ch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.lowestJunk = Double.parseDouble(content.toString().trim());
            telemetry.addData("Read lowest calibrate", this.lowestJunk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            StringBuilder content = new StringBuilder();
            try (FileReader reader = new FileReader("/sdcard/Logs/higher_calibrate.txt")) {
                int ch;
                while ((ch = reader.read()) != -1) {
                    content.append((char) ch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.highestJunk = Double.parseDouble(content.toString().trim());
            telemetry.addData("Read highest calibrate", this.highestJunk);
        } catch (Exception e) {
            e.printStackTrace();
        }


        telemetry.addLine("Waiting for start");
        telemetry.update();
        waitForStart();
        lowestJunk -= 0.5;
        highestJunk += 0.5;

        slide1.setPower(1);
        slide2.setPower(1);

//        camera.closeCameraDevice();
    }


    protected void grabSample() {
//        moveServo(arm, 0.91);
        maxArm();
        sleep(250);
        moveServo(claw, this.closedClaw);
        sleep(150);
    }
    protected void sampleInBasket() {
//        moveServo(arm, 0.76);
        moveServo(arm, 0.82);
//        moveServo(arm, 0.16);
        sleep(150);
        moveServo(claw, this.openClaw);
        sleep(150);
        restArm();
    }
    protected void restLift() {
        moveMotor(lift, 0, 1.5);
    }
    protected void topBasketLift() {
        moveMotor(lift, -4220, 1.5);
    }
    protected void restArm() {
//        moveServo(arm, 0.6094444444444445);
        moveServo(arm, this.minArm);
    }
    protected void maxArm() {
        moveServo(arm, this.maxArm);
    }
    // stub methods for later
    protected void placeSpecimen() {

    }


    // returns true if sensed, false if not
    protected boolean moveTillObjectSeen(boolean right, boolean timeout, double threshold) {
        if (lowestJunk == noLowestJunk) {
            lowestJunk = defaultLowestJunk;
            pTelem.addLine("Did not calibrate, going to default values");
        }
        if (highestJunk == noHighestJunk) {
            highestJunk = defaultHighestJunk;
            pTelem.addLine("Did not calibrate, going to default values");
        }

        pTelem.addLine("Using lowestJunk as " + lowestJunk);
        pTelem.addLine("Using highestJunk as " + highestJunk);
        pTelem.update();

        double distSeen = distanceSensor.getDistance(DistanceUnit.INCH);
//        pTelem.setData("distance", distSeen);
        pTelem.update();

        int beforeLf = lf_drive.getCurrentPosition();


        double horizontal = (right) ? 0.46 : -0.46;
        moveBot(threshold, 0, 0, horizontal);
        rf_drive.setPower(-horizontal);
        rb_drive.setPower(horizontal);
        lf_drive.setPower(horizontal);
        lb_drive.setPower(-horizontal);
//        sleep(400);

        boolean found = false;
//        ElapsedTime runtime = new ElapsedTime(); runtime.reset();
//        while (!found && opModeIsActive() && (timeout && runtime.seconds() < 4)) {
        while (!found && opModeIsActive()) {
            distSeen = distanceSensor.getDistance(DistanceUnit.INCH);
            /*
            if (distSeen < maxDist && (distSeen < lowestJunk || distSeen > highestJunk)) {
                removePower();
                List<Double> distancesWhile = new ArrayList<>();
                runTasksAsync(
                        () -> sleep(300),
                        () -> distancesWhile.add(distanceSensor.getDistance(DistanceUnit.INCH))
                );
                double avgDeviance = Math.abs(distancesWhile.stream().mapToDouble(Double::doubleValue).average().orElseThrow(() -> new IllegalArgumentException("Did not weigh currently seen dist")) - distSeen);
                if (avgDeviance < this.maxDistDeviance) {
                    pTelem.addLine("Confirmed object location with distance " + distSeen);
                    pTelem.addLine("And average " + avgDeviance);
                    pTelem.update();
                    break;
                }
            }*/
            if (distSeen < 7) {
                pTelem.addLine("seen distance: " + distSeen);
                pTelem.update();
                removePower();
                break;
            }
            rf_drive.setPower(-horizontal);
            rb_drive.setPower(horizontal);
            lf_drive.setPower(horizontal);
            lb_drive.setPower(-horizontal);
        }
//        pTelem.addLine(timeout && runtime.seconds() > 4 ? "Did not detect object" : "Done");
        pTelem.addLine("Done");
        pTelem.update();
        removePower();
//        sleep(300);
//        return timeout && runtime.seconds() > 4;
        return true;
    }
    protected boolean moveTillObjectSeen(boolean right, boolean timeout) {
        return moveTillObjectSeen(right, timeout, 0);
    }
    protected boolean moveTillObjectSeen(boolean right) {
        return moveTillObjectSeen(right, true, 0);
    }

    protected void goToSample() {
        int oldLf = lf_drive.getCurrentPosition();
        boolean seen = moveTillObjectSeen(false, true, 2.5);
//        sleep(100);
        /*
        if (!seen) {
            double distTraveled = ticsToInches(Math.abs(lf_drive.getCurrentPosition() - oldLf));
            if (distTraveled < 3) {
                moveBot(4 - distTraveled, 0, 0, -1);
            } else {
                moveBot(distTraveled - 3, 0, 0, 1);
            }
            moveBot(2.45, 0, 0, -1);
//            moveBot(2, 0, 0, 1);
//            moveBot(0.5, 0, 0, -1);
            moveBot(1, -1, 0, 0);
        }*/
        sleep(10);
        double distSeen = distanceSensor.getDistance(DistanceUnit.INCH);
        double distOffset = 7.5;
        double targetDist = distSeen < distOffset ? 0 : distSeen - distOffset;
        telemetry.addLine("targetDist at :" + targetDist);
//        moveBot(seen ? targetDist : 3, 1, 0, 0);
    }
    
    


    public abstract void runOpMode();


}
