package org.firstinspires.ftc.teamcode.auto.debug;

import org.firstinspires.ftc.teamcode.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.handlers.HandlerMap;
import org.firstinspires.ftc.teamcode.handlers.HardwareComponentHandler;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HardwareSnapshot implements Serializable {

    // Instead of using IntegerBounds encapsulation, track as longs
    private long start;
    private long end;
    private long offset = 0L;

    public final Map<HardwareComponentHandler<?>, Double> motorPoses = new IdentityHashMap<>();
    public final Map<DcMotorExHandler, Double> motorPowers = new IdentityHashMap<>();

    private final double voltage;

    public static final double NO_VOLTAGE = -1;
    private static final double minPowerThreshold = 0.05;

    // Recreation runtime variables



    private <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start, long end, long offset,
            Map<T, Double> motorPoses,
            Map<DcMotorExHandler, Double> motorPowers,
            double voltage) {
        this.start = start;
        this.end = end;
        this.offset = offset;
        this.motorPoses.clear();
        this.motorPoses.putAll(motorPoses);
        this.motorPowers.clear();
        this.motorPowers.putAll(motorPowers);
        this.voltage = voltage;
    }
    private <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start, long end, long offset,
            Map<T, Double> motorPoses,
            Map<DcMotorExHandler, Double> motorPowers) {
        this(start, end, offset, motorPoses, motorPowers, NO_VOLTAGE);
    }
    
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start,
            Map<T, Double> motorPoses,
            Map<DcMotorExHandler, Double> motorPowers,
            double voltage) {
        this(start, 0L, 0L, motorPoses, motorPowers, voltage);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start,
            Map<T, Double> motorPoses,
            Map<DcMotorExHandler, Double> motorPowers) {
        this(start, motorPoses, motorPowers, NO_VOLTAGE);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            Map<T, Double> motorPoses,
            Map<DcMotorExHandler, Double> motorPowers,
            double voltage) {
        this(System.nanoTime(), motorPoses, motorPowers, voltage);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            Map<T, Double> motorPoses,
            Map<DcMotorExHandler, Double> motorPowers) {
        this(motorPoses, motorPowers, NO_VOLTAGE);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start,
            Map<T, Double> motorPoses,
            boolean getPower,
            double voltage) {
        this(start,
                motorPoses,
                motorPoses.entrySet().stream()
                        .filter(e -> e.getKey() instanceof DcMotorExHandler)
                        .collect(Collectors.toMap(
                                e -> (DcMotorExHandler) e.getKey(),
                                e -> getPower ? 1d : ((DcMotorExHandler) e.getKey()).getPower()
                        )),
                voltage
        );
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start,
            Map<T, Double> motorPoses,
            boolean getPower) {
        this(start, motorPoses, getPower, NO_VOLTAGE);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start,
            Map<T, Double> motorPoses,
            double voltage) {
        this(start, motorPoses, false, voltage);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start,
            Map<T, Double> motorPoses) {
        this(start, motorPoses, NO_VOLTAGE);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            Map<T, Double> motorPoses,
            double voltage) {
        this(System.nanoTime(), motorPoses, voltage);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            Map<T, Double> motorPoses) {
        this(motorPoses, NO_VOLTAGE);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start,
            List<T> motors,
            double voltage) {
        this.start = start;
        motors.forEach(motor -> {
            motorPoses.put(motor, motor.getRelativePosition());
            if (motor instanceof DcMotorExHandler) {
                motorPowers.put((DcMotorExHandler) motor, ((DcMotorExHandler) motor).getPower());
            }
        });
        this.voltage = voltage;
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            long start,
            List<T> motors) {
        this(start, motors, NO_VOLTAGE);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            List<T> motors,
            double voltage) {
        this(System.nanoTime(), motors, voltage);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(
            List<T> motors) {
        this(motors, NO_VOLTAGE);
    }




    public HardwareSnapshot offset(long offset) {
        this.start -= offset;
        this.offset = offset;
        return this;
    }
    public HardwareSnapshot delay(long delay) {
        this.start += delay;
        this.end += delay;
        return this;
    }
    public void end(long end) {
        this.end = end;
    }
    public void end() {
        end(System.nanoTime() - offset);
    }

    // Accepts raw system times/offsetted times
    public boolean happening(long time) {
        return time >= start && time <= end;
    }
    public boolean willhappen(long time) {
        return time <= start;
    }
    public boolean happened(long time) {
        return time > end;
    }


    public void recreate(HardwareContext context) {
        Map<DcMotorExHandler, Double> herePowers = new HashMap<>(motorPowers);
        motorPoses.forEach((k, v) -> {
            long start = System.nanoTime();
            if (!(k instanceof DcMotorExHandler)) {
                k.setPosition(v);
                return;
            }
            DcMotorExHandler kD = (DcMotorExHandler) k;
            double power = (herePowers.containsKey(k) ? herePowers.remove(k) : motorPowers.getOrDefault(kD, 0d));
            if (context.hasVoltage()) {
                if (voltage == NO_VOLTAGE && (!context.hasPrev() || context.prevVoltage() == NO_VOLTAGE)) {
                    kD.setPower(power);
                    return;
                }
                power *= ((context.voltage()) / (voltage == NO_VOLTAGE ? context.prevVoltage() : voltage));
            }
            long elapsed = System.nanoTime() - start;
            this.end += elapsed;
            context.offset(elapsed);
            kD.setPower(power);
//          kD.setPosition(v, motorPowers.get(kD));
        });
        herePowers.forEach(DcMotorExHandler::setPower);
    }
    public void recreate() {
        recreate(HardwareContext.empty());
    }




    private <T extends HardwareComponentHandler<?>> String serializeMap(Map<T, Double> map) {
        return map.entrySet().stream().map(e -> e.getKey().getName() + ": " + e.getValue()).collect(Collectors.joining(", "));
    }

    public String serialize() {
        return "[(" + start + ", " + end + (offset != 0L ? ", " + offset : "") + "), (" + voltage + "), {" + serializeMap(motorPoses) + "}, {" + serializeMap(motorPowers) + "}]";
    }
    public static HardwareSnapshot deserialize(String serialized) {
        Pattern serialWrapper = Pattern.compile(
                "\\[\\((\\d+), (\\d+)(?:, (\\d+))?\\)\\s*(?:, \\((-?\\d+(?:\\.\\d+)?)\\))?\\s*, \\{(.*?)\\}, \\{(.*?)\\}]"
        );

        Matcher mainMatcher = serialWrapper.matcher(serialized);
        if (!mainMatcher.matches()) {
            throw new IllegalArgumentException("Invalid serialized string format");
        }

        long start = Long.parseLong(Objects.requireNonNull(mainMatcher.group(1)));
        long end = Long.parseLong(Objects.requireNonNull(mainMatcher.group(2)));
        long offset = Long.parseLong(Optional.ofNullable(mainMatcher.group(3)).orElse("0"));
        String voltageStr = mainMatcher.group(4);
        double voltage = (voltageStr == null || voltageStr.isEmpty()) ? NO_VOLTAGE : Double.parseDouble(voltageStr);

        String motorPosesStr = mainMatcher.group(5);
        String motorPowersStr = mainMatcher.group(6);
        if (motorPosesStr == null && motorPowersStr == null) {
            throw new IllegalArgumentException("No maps specified");
        }

        Map<HardwareComponentHandler<?>, Double> motorPoses = new HashMap<>();
        if (motorPosesStr != null && !motorPosesStr.trim().isEmpty()) {
            Arrays.stream(motorPosesStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.split(":"))
                    .forEach(motor -> {
                        if (motor.length != 2) throw new IllegalArgumentException("Invalid motor pos format");
                        motorPoses.put(HandlerMap.get(motor[0].trim()),
                                Double.parseDouble(Objects.requireNonNull(motor[1].trim())));
                    });
        }

        Map<DcMotorExHandler, Double> motorPowers = new HashMap<>();
        if (motorPowersStr != null && !motorPowersStr.trim().isEmpty()) {
            Arrays.stream(motorPowersStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.split(":"))
                    .forEach(motor -> {
                        if (motor.length != 2) throw new IllegalArgumentException("Invalid motor power format");
                        motorPowers.put((DcMotorExHandler) HandlerMap.get(motor[0].trim()),
                                Double.parseDouble(Objects.requireNonNull(motor[1].trim())));
                    });
        }

        HardwareSnapshot snapshot = new HardwareSnapshot(start, end, offset, motorPoses, motorPowers, voltage);
        snapshot.motorPoses.putAll(motorPoses);
        snapshot.motorPowers.putAll(motorPowers);
        return snapshot;
    }

    public static HardwareSnapshot copy(HardwareSnapshot state) {
        return new HardwareSnapshot(System.nanoTime(), state.end != 0L ? (state.end - state.start) + System.nanoTime() : 0L, state.offset, state.motorPoses, state.motorPowers);
    }
    public static HardwareSnapshot merge(HardwareSnapshot start, HardwareSnapshot end) {
        return new HardwareSnapshot(start.start, end.end, end.offset, end.motorPoses, end.motorPowers);
    }

    public Map<DcMotorExHandler, Double> motorPowers() {
        return Collections.unmodifiableMap(motorPowers);
    }
    public Map<HardwareComponentHandler<?>, Double> motorPositions() {
        return Collections.unmodifiableMap(motorPoses);
    }

    public double voltage() {
        return this.voltage;
    }
    public long duration() {
        return end - start;
    }

    public boolean equals(HardwareSnapshot state) {
        return state.motorPowers.entrySet().stream().allMatch(e -> Objects.equals(motorPowers.get(e.getKey()), e.getValue()));
    }


}
