package org.firstinspires.ftc.teamcode.teleop.drive;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.FileLogger;
import org.firstinspires.ftc.teamcode.HardwareSnapshot;
import org.firstinspires.ftc.teamcode.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.handlers.HardwareComponentHandler;
import org.firstinspires.ftc.teamcode.handlers.controller.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.firstinspires.ftc.teamcode.util.Async.*;

import android.os.Environment;

@TeleOp(name = "DebugJava")
public class DebugJava extends DriveJava {


    private final List<DcMotorExHandler> movementMotors = List.of(lf_drive, lb_drive, rf_drive, lb_drive);
    private final List<HardwareComponentHandler<?>> otherDevices = List.of(lift, arm, slide1, slide2, slideArm, claw);
    private final Map<DcMotorExHandler, Double> strictMMOffsets = new HashMap<>();


    private final List<ControllerListener> strictMMs = List.of(
            ToggleListener.of(() -> gamepad1.x, this::startStrictMM, this::logStrictMM)
    );
    private final List<Supplier<Boolean>> otherListeners = List.of(
            () -> isControlled(gamepad2.left_stick_y), () -> isControlled(gamepad2.right_stick_y)
                   , () -> gamepad2.dpad_left, () -> gamepad2.dpad_right
                   , () -> isControlled(gamepad2.left_trigger), () -> isControlled(gamepad2.right_trigger)
                   , () -> gamepad2.left_bumper, () -> gamepad2.right_bumper
    );
    private final List<ControllerListener> omListeners = List.of(
            InitListener.of(() -> otherListeners.stream().anyMatch(Supplier::get), this::startOM),
            FinishListener.of(() -> otherListeners.stream().anyMatch(s -> !s.get()), this::logOM)
    );


    // Runtime variables
    private String movementMode = "STRICT";
    private String strictMode = "STRAIGHT";
    private boolean logEnabled = true;
    private long startTime = 0L;

    private final List<HardwareSnapshot> mmLogs = new ArrayList<>();
    private final List<HardwareSnapshot> omLogs = new ArrayList<>();
    // Store last HardwareSnapshot in a separate variable so that it does not have
    // to be repeatedly retrieved as the last element from the mmLogs list
    private HardwareSnapshot lastMMState, lastOMState;
    private long mmStartTime = 0L, omStartTime = 0L;

    private FileLogger mmLogger, omLogger;
    private ScheduledExecutorService logScheduler;
    private ScheduledFuture<?> logTask;
    private static final long LOG_INTERVAL = 2;




    @Override
    public void predrive() {
        telemetry.addLine("Initiating debug pipeline...");

        movementMotors.forEach(DcMotorExHandler::resetEncoder);
        try {
            String dateName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date());

            this.mmLogger = new FileLogger(String.format(logsPath + "debug_mm_%s.txt", dateName));
            this.omLogger = new FileLogger(String.format(logsPath + "debug_om_%s.txt", dateName));
        } catch (IOException e) {
            telemetry.addLine("Failed to init debug file");
            e.printStackTrace();
        }
        this.listeners = List.of(
                InitListener.of(() -> gamepad1.b, this::log)
        );
        this.logScheduler = Executors.newSingleThreadScheduledExecutor();
        this.logTask = logScheduler.scheduleWithFixedDelay(this::log, LOG_INTERVAL, LOG_INTERVAL, TimeUnit.SECONDS);

        telemetry.addLine("Initiated debug pipeline!");
    }

    @Override
    public void drive() {

        if (isControlled(gamepad1.right_trigger)) {
            logEnabled = true;
        } else if (isControlled(gamepad1.left_trigger)) {
            logEnabled = false;
        }
        if (!logEnabled) {
            telemetry.addLine("Log disabled, press right trigger to enable");
            telemetry.update();
            return;
        }
        if (startTime == 0L) {
            startTime = System.nanoTime();
        }

        // Buttons to change modes
        if (gamepad1.a) {
            strictMode = "STRAIGHT";
        }
        if (gamepad1.y) {
            strictMode = "TURN";
        }
        if (gamepad1.dpad_up) {
            movementMode = "STRICT";
        }
        if (gamepad1.dpad_down) {
            movementMode = "FREE";
        }
        if (gamepad1.dpad_left) {
            strictMode = "STRAIGHT";
        }
        if (gamepad1.dpad_right) {
            strictMode = "TURN";
        }


        super.commonDrive();
        if (movementMode.equals("STRICT")) {
            bubbleListeners(strictMMs);
            moveBot(strictMode.equals("TURN") ? 0 : -gamepad1.left_stick_y,
                    strictMode.equals("STRAIGHT") ? 0 : gamepad1.right_stick_x,
                    strictMode.equals("TURN") ? 0 : gamepad1.left_stick_x);
        } else if (movementMode.equals("FREE")) {
            runTasksAsync(
                    () -> moveBot(-gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x),
                    this::logFreeMM
            );
        }
        bubbleListeners(omListeners);


        telemetry.addLine("Manual:");
        telemetry.addLine("All debug controls are on gamepad 1");
        telemetry.addLine("Change driving mode with dpad up (STRICT), dpad down (FREE)");
        telemetry.addLine("Change strict mode with dpad left (STRAIGHT), dpad right (TURN)");
        telemetry.addLine("Start/end strict logging with X");
        telemetry.addLine("Manually log with B");
        telemetry.addLine("Enable log with right trigger, disable log with left trigger");
        telemetry.addLine("");
        telemetry.addData("Movement mode",  movementMode);
        telemetry.addData("Strict mode",  strictMode);
        telemetry.addData("Current start", startTime);
        telemetry.addData("Current other start", omStartTime);
        telemetry.update();

    }

    @Override
    public void postdrive() {
        logTask.cancel(true);
        logScheduler.shutdownNow();
        this.log();
        this.mmLogger.close();
        this.omLogger.close();
        telemetry.addLine("Logger close");
        telemetry.update();
        super.postdrive();
    }




    public void log() {
        // For loop instead of streams to catch errors
        for (HardwareSnapshot mmSnapshot : mmLogs) {
            try {
                mmLogger.logData(mmSnapshot.serialize());
            } catch (IOException e) {
                telemetry.addLine("Failed to log movement data. Details:");
                e.printStackTrace();
                return;
            }
        }
        for (HardwareSnapshot omSnapshot : omLogs) {
            try {
                omLogger.logData(omSnapshot.serialize());
            } catch (IOException e) {
                telemetry.addLine("Failed to log other movement data. Details:");
                e.printStackTrace();
                return;
            }
        }
        mmLogs.clear();
        omLogs.clear();
    }

    private void startOM() {
        this.omStartTime = System.nanoTime();
    }

    private void logOM() {
        HardwareSnapshot omState = new HardwareSnapshot(omStartTime, otherDevices).offset(startTime);
        if (this.lastOMState.equals(omState)) return;

        this.lastOMState.end();
        this.lastOMState = omState;
        omLogs.add(this.lastOMState);
    }


    private void logFreeMM() {
        HardwareSnapshot mmState = new HardwareSnapshot(movementMotors).offset(startTime);
        if (this.lastMMState.equals(mmState)) return;

        this.lastMMState.end();
        this.lastMMState = mmState;
        mmLogs.add(this.lastMMState);
    }

    private void startStrictMM() {
        this.mmStartTime = System.nanoTime();
        movementMotors.forEach(motor -> strictMMOffsets.put(motor, motor.getPosition()));
    }

    private void logStrictMM() {
        Map<DcMotorExHandler, Double> freeMMPoses = movementMotors.stream()
                .map(m -> new AbstractMap.SimpleEntry<>(m, m.getPosition() - strictMMOffsets.get(m)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        HardwareSnapshot freeMMState = new HardwareSnapshot(this.mmStartTime, freeMMPoses);
        freeMMState.end();
        mmLogs.add(freeMMState);
    }

}
