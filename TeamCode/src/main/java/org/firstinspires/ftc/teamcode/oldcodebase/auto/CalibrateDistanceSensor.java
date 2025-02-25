package org.firstinspires.ftc.teamcode.oldcodebase.auto;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.oldcodebase.CustomTelemetryLogger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Autonomous(name = "CalibrateDistanceSensor", group = "Auto")
public class CalibrateDistanceSensor extends AutoJava {

    public CalibrateDistanceSensor() {
        super(true);
    }

    @Override
    public void runOpMode() {
        this.initMotors();
        distanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distancesensor");
        if (distanceSensor != null) {
            distanceSensor.initialize();
        }
        while (opModeIsActive()) {
//            if ((gamepad1.right_trigger > 0.1 || gamepad2.right_trigger > 0.1)  && distanceSensor != null) {
                telemetry.addLine("Calibrating");
                double currDist = distanceSensor.getDistance(DistanceUnit.INCH);
//                if (currDist < maxDist) {
                if (currDist < lowestJunk) {
                    lowestJunk = currDist;
                } else if (currDist > highestJunk && currDist < 300) {
                    highestJunk = currDist;
                }
//                }
//            }
            telemetry.addLine("Curr dist seen: " + distanceSensor.getDistance(DistanceUnit.INCH));
            telemetry.addLine("Current lowest junk: " + lowestJunk);
            telemetry.addLine("Current highest junk: " + highestJunk);
            telemetry.update();
        }
        try {
            CustomTelemetryLogger calibrateLower = new CustomTelemetryLogger("/sdcard/Logs/lower_calibrate.txt", false);
            calibrateLower.logDataRaw(String.valueOf(lowestJunk));
            calibrateLower.close();
            telemetry.addLine("Wrote lowest calibrate");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            CustomTelemetryLogger calibrateHigher = new CustomTelemetryLogger("/sdcard/Logs/higher_calibrate.txt", false);
            calibrateHigher.logDataRaw(String.valueOf(highestJunk));
            calibrateHigher.close();
            telemetry.addLine("Wrote lowest calibrate");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        telemetry.update();


    }
}
