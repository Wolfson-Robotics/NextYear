package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.HardwareSnapshot;
import org.firstinspires.ftc.teamcode.PersistentTelemetry;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.util.Async;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.firstinspires.ftc.teamcode.util.Async.sleep;

@Autonomous(name = "InstantReplay", group = "Auto")
public class InstantReplay extends RobotBase {

    private List<HardwareSnapshot> mmSnapshots, omSnapshots;
    private int initMMSize, initOMSize;
    private final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);

    private List<HardwareSnapshot> parseLog(String type) {

        String instructName = type + "_instruct.txt";
        telemetry.addLine("Which file would you like to load?");
        telemetry.addLine("Press gamepad1 a for " + instructName);
        telemetry.addLine("Press gamepad1 b for latest " + type + " log");
        telemetry.update();

        String logPath = "";
        while (logPath.isEmpty()) {
            if (gamepad1.a) {
                telemetry.addLine("Loading " + instructName + "...");
                telemetry.update();
                logPath = storagePath + "/autonomous/" + instructName;
            }
            if (gamepad1.b) {
                File[] logFiles = new File(logsPath).listFiles((dir, name) -> name.startsWith("debug_" + type + "_"));
                if (logFiles == null || logFiles.length == 0) {
                    telemetry.addLine("No logs found");
                    telemetry.update();
                    sleep(10000);
                    this.terminateOpModeNow();
                    return null;
                }

                telemetry.addLine("Loading latest " + type + " log...");
                telemetry.update();
                Arrays.sort(logFiles, (f1, f2) -> f2.getName().compareTo(f1.getName()));
                logPath = logsPath + "/" + logFiles[0];
            }
        }


        FileReader fileReader;
        try {
            fileReader = new FileReader(logPath);
        } catch (FileNotFoundException e) {
            telemetry.addLine("Log path " + logPath + " not found");
            telemetry.update();
            sleep(10000);
            this.terminateOpModeNow();
            return null;
        }

        List<HardwareSnapshot> snapshots = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                snapshots.add(HardwareSnapshot.deserialize(line));
            }
        } catch (IOException e) {
            telemetry.addLine("Failed to read log file " + logPath + ". Details:");
            e.printStackTrace();
            sleep(10000);
            this.terminateOpModeNow();
            return null;
        }

        return snapshots;
    }

    @Override
    public void init() {
        this.mmSnapshots = this.parseLog("mm");
        this.omSnapshots = this.parseLog("om");
        if (mmSnapshots == null) {
            telemetry.addLine("MM snapshots could not be loaded, aborting");
            telemetry.update();
            sleep(10000);
            return;
        }
        if (omSnapshots == null) {
            telemetry.addLine("OM snapshots could not be loaded, aborting");
            telemetry.update();
            sleep(5000);
            return;
        }
        this.initMMSize = mmSnapshots.size();
        this.initOMSize = omSnapshots.size();

        pTelem.addLine("Starting replay...");
        pTelem.update();
    }


    @Override
    public void start() {

        long start = System.nanoTime();

        Thread mmThread = new Thread(() -> {
            if (mmSnapshots.isEmpty()) {
                Thread.currentThread().interrupt();
                return;
            }
            HardwareSnapshot currentSnapshot = mmSnapshots.get(0);
            mmSnapshots.remove(0);
            Async.async(currentSnapshot::recreate);

            pTelem.setData("MM snapshots left", mmSnapshots.size() - initMMSize);
            pTelem.update();

            while (currentSnapshot.happened(System.nanoTime() - start)) {
                Thread.yield();
            }
        });

        Thread omThread = new Thread(() -> {
            if (omSnapshots.isEmpty()) {
                Thread.currentThread().interrupt();
                return;
            }
            HardwareSnapshot currentSnapshot = omSnapshots.get(0);
            omSnapshots.remove(0);
            Async.async(currentSnapshot::recreate);

            pTelem.setData("OM snapshots left", omSnapshots.size() - initOMSize);
            pTelem.update();

            while (currentSnapshot.happened(System.nanoTime() - start)) {
                Thread.yield();
            }
        });

        mmThread.start();
        omThread.start();

        try {
            mmThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            omThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void loop() {

    }

}
