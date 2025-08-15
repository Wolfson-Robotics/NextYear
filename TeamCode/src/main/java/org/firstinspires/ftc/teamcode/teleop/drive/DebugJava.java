package org.firstinspires.ftc.teamcode.teleop.drive;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.FileLogger;
import org.firstinspires.ftc.teamcode.auto.debug.HardwareSnapshot;
import org.firstinspires.ftc.teamcode.PersistentTelemetry;
import org.firstinspires.ftc.teamcode.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.handlers.HardwareComponentHandler;
import org.firstinspires.ftc.teamcode.handlers.controller.ActiveListener;
import org.firstinspires.ftc.teamcode.handlers.controller.ControllerListener;
import org.firstinspires.ftc.teamcode.handlers.controller.HoldListener;
import org.firstinspires.ftc.teamcode.handlers.controller.InitListener;
import org.firstinspires.ftc.teamcode.handlers.controller.StickyListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@TeleOp(name = "DebugJava")
public class DebugJava extends DriveJava {


    private List<DcMotorExHandler> movementMotors;
    private List<HardwareComponentHandler<?>> otherDevices;
    private final Map<DcMotorExHandler, Double> strictMMOffsets = new HashMap<>();

    private final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);

    private final List<ControllerListener> strictMMs = List.of(
            StickyListener.of(() -> gamepad1.x, () -> {
                pTelem.setData("Start/end", true);
                this.startStrictMM();
            }, () -> {
                pTelem.setData("Start/end", false);
                this.logStrictMM();
            })
    );
    private final List<Supplier<Boolean>> freeMMListeners = List.of(
            () -> isControlled(gamepad1.left_stick_x), () -> isControlled(gamepad1.left_stick_y),
            () -> isControlled(gamepad1.right_stick_x)
    );
    private final List<ControllerListener> freeMMs = List.of(
            ActiveListener.of(() -> freeMMListeners.stream().anyMatch(Supplier::get), this::logFreeMM)
    );
    private final List<Supplier<Boolean>> otherListeners = List.of(
                    () -> isControlled(gamepad2.left_stick_y), () -> isControlled(gamepad2.right_stick_y)
            , () -> gamepad2.dpad_left, () -> gamepad2.dpad_right
            , () -> isControlled(gamepad2.left_trigger), () -> isControlled(gamepad2.right_trigger)
            , () -> gamepad2.left_bumper, () -> gamepad2.right_bumper
    );
    private final List<ControllerListener> omListeners = List.of(
            HoldListener.of(() -> otherListeners.stream().anyMatch(Supplier::get), this::startOM, this::logOM)
    );


    // Runtime variables
    private String movementMode = "STRICT";
    private String strictMode = "STRAIGHT";
    private boolean logEnabled = true;
    private long startTime = 0L;

    private final ConcurrentLinkedQueue<HardwareSnapshot> mmLogs = new ConcurrentLinkedQueue<>(), omLogs = new ConcurrentLinkedQueue<>();
    // Store last HardwareSnapshot in a separate variable so that it does not have
    // to be repeatedly retrieved as the last element from the mmLogs list
    private HardwareSnapshot lastMMState, lastOMState;
    private long mmStartTime = 0L, omStartTime = 0L;

    private final AtomicInteger mmLogCounter = new AtomicInteger(0), omLogCounter = new AtomicInteger(0),
            mmMergeCounter = new AtomicInteger(0);

    private String mmName, omName;
    private ExecutorService movementCapture;



    private void initStrict() {
        if (this.lastMMState != null) {
            this.logFreeMM();
            this.lastMMState = null;
        }
        if (this.movementCapture != null) return;
        this.movementCapture = Executors.newFixedThreadPool(strictMMs.size() + omListeners.size());
        for (ControllerListener l : strictMMs) movementCapture.submit(toPersistentThread(l::update));
        for (ControllerListener l : omListeners) movementCapture.submit(toPersistentThread(l::update));
    }
    private void initFree() {
        if (this.movementCapture != null) return;
        this.movementCapture = Executors.newFixedThreadPool(freeMMs.size() + omListeners.size());
//        for (ControllerListener l : freeMMs) movementCapture.submit(toPersistentThread(l::update));
        movementCapture.submit(toPersistentThread(this::logFreeMM));
        for (ControllerListener l : omListeners) movementCapture.submit(toPersistentThread(l::update));
    }
    private void stopLogThreads() {
        if (this.movementCapture != null) {
            this.movementCapture.shutdownNow();
            this.movementCapture = null;
        }
    }

    @Override
    public void init() {
        super.init();
        this.movementMotors = List.of(lf_drive, lb_drive, rf_drive, rb_drive);
//        this.otherDevices = List.of(lift, arm, /*slide1, slide2,*/ slideArm, claw);
        this.otherDevices = List.of(arm, claw);

        telemetry.addLine("Initiating debug pipeline...");

        movementMotors.forEach(DcMotorExHandler::resetEncoder);

        String dateName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date());
        this.mmName = String.format(logsPath + "debug_mm_%s.txt", dateName);
        this.omName = String.format(logsPath + "debug_om_%s.txt", dateName);

        // Populate PersistentTelemetry with lines
        pTelem.addLine("Manual:");
        pTelem.addLine("All debug controls are on gamepad 1");
        pTelem.addLine("Change driving mode with dpad up (STRICT), dpad down (FREE)");
        pTelem.addLine("Change strict mode with dpad left (STRAIGHT), dpad right (TURN)");
        pTelem.addData("Start/end strict logging with X (started", false);
        pTelem.addLine("Manually log with B");
        pTelem.addLine("Enable log with right trigger, disable log with left trigger");
        pTelem.addLine("");
        pTelem.addData("Movement mode", () -> movementMode);
        pTelem.addData("Strict mode", () -> strictMode);
        pTelem.addData("Current start", () -> startTime);
        pTelem.addData("Current other start", () -> omStartTime);
        pTelem.addLine(" ");
        pTelem.addLine(" ");
        pTelem.addData("Total MM snapshots taken", () -> mmLogCounter.get() + mmLogs.size());
        pTelem.addData("Total OM snapshots taken", () -> omLogCounter.get() + omLogs.size());
        pTelem.addData("MM snapshots logged", mmLogCounter::get);
        pTelem.addData("MM snapshots merged", mmMergeCounter::get);
        pTelem.addData("OM snapshots logged", omLogCounter::get);
        pTelem.addLine(" ");
        pTelem.addLine("Runtime variables");
        pTelem.addLine("------------------------------");
        pTelem.addData("Listener thread running", !Optional.ofNullable(cThread).map(CompletableFuture::isDone).orElse(false));

        telemetry.addLine("Initiated debug pipeline!");
    }

    @Override
    public void start() {
        super.start();
        initStrict();
    }

    @Override
    public void drive() {

        if (isControlled(gamepad1.right_trigger)) {
            initStrict();
            logEnabled = true;
        } else if (isControlled(gamepad1.left_trigger)) {
            stopLogThreads();
            logEnabled = false;
        }
        if (!logEnabled) {
            telemetry.addLine("Log disabled, press right trigger to enable");
            telemetry.addLine("");
//            telemetry.update();
            super.drive();
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
        if (gamepad1.dpad_up && !movementMode.equals("STRICT")) {
            stopLogThreads();
            initStrict();
            movementMode = "STRICT";
        }
        if (gamepad1.dpad_down && !movementMode.equals("FREE")) {
            stopLogThreads();
            initFree();
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
            moveBot(strictMode.equals("TURN") ? 0 : -gamepad1.left_stick_y,
                    strictMode.equals("STRAIGHT") ? 0 : gamepad1.right_stick_x,
                    strictMode.equals("TURN") ? 0 : gamepad1.left_stick_x);
        } else if (movementMode.equals("FREE")) {
            moveBot(-gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
        }
        pTelem.update();
    }

    @Override
    public void stop() {
        telemetry.addLine("Logging one last time");
        telemetry.update();
        this.log();

        telemetry.addLine("Stopping");
        telemetry.update();
        stopLogThreads();
        super.stop();
    }




    public void log() {
        try {
            pTelem.appendLine("Manually log", ", logging mm");
            FileLogger mmLogger = new FileLogger(mmName);

            HardwareSnapshot firstEqu = null, lastEqu = null;
            for (HardwareSnapshot mmSnapshot : mmLogs) {
                if (firstEqu == null) {
                    firstEqu = mmSnapshot;
                    lastEqu = mmSnapshot;
                    continue;
                }
                else if (firstEqu.equals(mmSnapshot)) {
                    lastEqu = mmSnapshot;
                    mmMergeCounter.incrementAndGet();
                    continue;
                }
                mmLogger.logData((lastEqu != null ? HardwareSnapshot.merge(firstEqu, lastEqu) : mmSnapshot).serialize());
                firstEqu = mmSnapshot;
                lastEqu = null;
                mmLogCounter.incrementAndGet();
            }
            if (lastEqu != null) mmLogger.logData(HardwareSnapshot.merge(firstEqu, lastEqu).serialize());
            else if (firstEqu != null) mmLogger.logData(firstEqu.serialize());
            mmLogger.close();

            pTelem.appendLine("Manually log", ", logged mm");
            mmLogs.clear();
        } catch (IOException e) {
            telemetry.addLine("Failed to log movement data. Details:");
            e.printStackTrace();
        }

        try {
            pTelem.appendLine("Manually log", ", logging om");
            FileLogger omLogger = new FileLogger(omName);
            for (HardwareSnapshot omSnapshot : omLogs) {
                omLogger.logData(omSnapshot.serialize());
                omLogCounter.incrementAndGet();
            }
            omLogger.close();
            pTelem.appendLine("Manually log", ", logged om");
            omLogs.clear();
        } catch (IOException e) {
            telemetry.addLine("Failed to log other movement data. Details:");
            e.printStackTrace();
        }
//        pTelem.setLine("Manually log", "Manually log with B");
    }


    private void startOM() {
        this.omStartTime = System.nanoTime();
    }

    private void logOM() {
        HardwareSnapshot omState = new HardwareSnapshot(omStartTime, otherDevices).offset(startTime);
        if (this.lastOMState != null) {
            if (this.lastOMState.equals(omState)) return;
            this.lastOMState.end();
        }
        this.lastOMState = omState;
        omLogs.add(this.lastOMState);
    }


    // Delay mmLog appendage and HardwareSnapshot creation to allow for timelapse
    private void logFreeMM() {
        if (this.lastMMState != null) {
            this.lastMMState.end();
            mmLogs.add(this.lastMMState);
        }
        this.lastMMState = new HardwareSnapshot(movementMotors, getVoltage()).offset(startTime);
    }

    private void startStrictMM() {
        this.mmStartTime = System.nanoTime();
        movementMotors.forEach(motor -> strictMMOffsets.put(motor, motor.getRelativePosition()));
    }

    private void logStrictMM() {
        Map<DcMotorExHandler, Double> freeMMPoses = movementMotors.stream()
                .collect(Collectors.toMap(
                        m -> m,
                        m -> m.getRelativePosition() - strictMMOffsets.get(m),
                        (a, b) -> a,
                        HashMap::new
                ));

        HardwareSnapshot freeMMState = new HardwareSnapshot(this.mmStartTime, freeMMPoses);
        freeMMState.end();
        mmLogs.add(freeMMState);
    }


    public List<ControllerListener> listeners() {
        return List.of(
                InitListener.of(() -> gamepad1.b, this::log)
        );
    }

}
