package org.firstinspires.ftc.teamcode.teleop.drive;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.FileLogger;
import org.firstinspires.ftc.teamcode.HardwareSnapshot;
import org.firstinspires.ftc.teamcode.PersistentTelemetry;
import org.firstinspires.ftc.teamcode.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.handlers.HardwareComponentHandler;
import org.firstinspires.ftc.teamcode.handlers.controller.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.firstinspires.ftc.teamcode.util.Async.*;

@TeleOp(name = "DebugJava")
public class DebugJava extends DriveJava {


    private List<DcMotorExHandler> movementMotors;
    private List<HardwareComponentHandler<?>> otherDevices;
    private final Map<DcMotorExHandler, Double> strictMMOffsets = new HashMap<>();


    private final List<ControllerListener> strictMMs = List.of(
            StickyListener.of(() -> gamepad1.x, this::startStrictMM, this::logStrictMM)
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

    private String mmName, omName;
    private ScheduledExecutorService logScheduler;
    private ScheduledFuture<?> logTask;
    private static final long LOG_INTERVAL = 2;

    private final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);
    



    @Override
    public void init() {
        super.init();
        this.movementMotors = List.of(lf_drive, lb_drive, rf_drive, lb_drive);
//        this.otherDevices = List.of(lift, arm, /*slide1, slide2,*/ slideArm, claw);
        this.otherDevices = List.of(arm, claw);

        telemetry.addLine("Initiating debug pipeline...");

        movementMotors.forEach(DcMotorExHandler::resetEncoder);
        this.listeners = List.of(
                InitListener.of(() -> gamepad1.b, this::log)
        );
        this.logScheduler = Executors.newSingleThreadScheduledExecutor();

        String dateName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date());
        this.mmName = String.format(logsPath + "debug_mm_%s.txt", dateName);
        this.omName = String.format(logsPath + "debug_mm_%s.txt", dateName);

        // Populate PersistentTelemetry with lines
        pTelem.addLine("Manual:");
        pTelem.addLine("All debug controls are on gamepad 1");
        pTelem.addLine("Change driving mode with dpad up (STRICT), dpad down (FREE)");
        pTelem.addLine("Change strict mode with dpad left (STRAIGHT), dpad right (TURN)");
        pTelem.addLine("Start/end strict logging with X");
        pTelem.addLine("Manually log with B");
        pTelem.addLine("Enable log with right trigger, disable log with left trigger");
        pTelem.addLine("");
        pTelem.addData("Movement mode",  movementMode);
        pTelem.addData("Strict mode",  strictMode);
        pTelem.addData("Current start", startTime);
        pTelem.addData("Current other start", omStartTime);
        pTelem.addLine(" ");
        pTelem.addLine(" ");
        pTelem.addLine(" ");
        pTelem.addLine(" ");
        pTelem.addLine("Runtime variables");
        pTelem.addLine("------------------------------");
        pTelem.addData("Listener thread running", !Optional.ofNullable(cThread).map(CompletableFuture::isDone).orElse(false));

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
            if (logTask != null) logTask.cancel(false);
            telemetry.addLine("Log disabled, press right trigger to enable");
            telemetry.update();
            return;
        }
        if (logTask == null) {
//            logTask = logScheduler.scheduleWithFixedDelay(this::log, 0, LOG_INTERVAL, TimeUnit.SECONDS);
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
//            bubbleListeners(strictMMs);
            listeners.forEach(ControllerListener::update);
            strictMMs.forEach(ControllerListener::update);
            moveBot(strictMode.equals("TURN") ? 0 : -gamepad1.left_stick_y,
                    strictMode.equals("STRAIGHT") ? 0 : gamepad1.right_stick_x,
                    strictMode.equals("TURN") ? 0 : gamepad1.left_stick_x);
        } else if (movementMode.equals("FREE")) {
//            moveBot(-gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
//            this.logFreeMM();
//            runTasksAsync(
//                    () -> moveBot(-gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x),
//                    this::logFreeMM
//            );
        }
       if (gamepad1.right_bumper) {
           this.lf_drive.setPower(0.3);
           this.lb_drive.setPower(0.3);
           this.rb_drive.setPower(0.3);
           this.rf_drive.setPower(0.3);
           sleep(200);
           this.rf_drive.setPower(0);
       }
//       if (this.logEnabled) log();
//        bubbleListeners(omListeners);


        pTelem.update();

    }

    @Override
    public void stop() {
//        logTask.cancel(true);
//        logScheduler.shutdownNow();

        telemetry.addLine("Logging finally");
        telemetry.update();
        this.log();

        telemetry.addLine("Stopping");
        telemetry.update();
        super.stop();
//        super.terminateOpModeNow();
    }




    public void log() {
        try {
            pTelem.appendLine("Manually log", ", logging mm");
            FileLogger mmLogger = new FileLogger(mmName);
            for (HardwareSnapshot mmSnapshot : mmLogs) mmLogger.logData(mmSnapshot.serialize());
            mmLogger.close();
            pTelem.appendLine("Manually log", ", logged mm");
        } catch (IOException e) {
            telemetry.addLine("Failed to log movement data. Details:");
            e.printStackTrace();
        }

        try {
            pTelem.appendLine("Manually log", ", logging om");
            FileLogger omLogger = new FileLogger(omName);
            for (HardwareSnapshot omSnapshot : omLogs) omLogger.logData(omSnapshot.serialize());
            omLogger.close();
            pTelem.appendLine("Manually log", ", logged om");
        } catch (IOException e) {
            telemetry.addLine("Failed to log other movement data. Details:");
            e.printStackTrace();
        }
//        pTelem.setLine("Manually log", "Manually log with B");
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
        if (this.lastMMState != null) {
            if (this.lastMMState.equals(mmState)) return;
            this.lastMMState.end();
            mmLogs.add(this.lastMMState);
        }
        this.lastMMState = mmState;

    }

    private void startStrictMM() {
        this.mmStartTime = System.nanoTime();
        movementMotors.forEach(motor -> strictMMOffsets.put(motor, motor.getPosition()));
    }

    private void logStrictMM() {
        Map<DcMotorExHandler, Double> freeMMPoses = movementMotors.stream()
                .collect(Collectors.toMap(
                        m -> m,
                        m -> m.getPosition() - strictMMOffsets.get(m),
                        (a, b) -> a,
                        HashMap::new
                ));

        HardwareSnapshot freeMMState = new HardwareSnapshot(this.mmStartTime, freeMMPoses);
        freeMMState.end();
        mmLogs.add(freeMMState);
    }

}
