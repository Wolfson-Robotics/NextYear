package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.handlers.HandlerMap;
import org.firstinspires.ftc.teamcode.handlers.HardwareComponentHandler;

import java.io.Serializable;
import java.util.HashMap;
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

    private final Map<HardwareComponentHandler<?>, Double> motorPoses = new HashMap<>();
    private final Map<DcMotorExHandler, Double> motorPowers = new HashMap<>();


    private <T extends HardwareComponentHandler<?>> HardwareSnapshot(long start, long end, long offset,
                                                                     Map<T, Double> motorPoses, Map<DcMotorExHandler, Double> motorPowers) {
        this.start = start;
        this.end = end;
        this.offset = offset;
        this.motorPoses.clear();
        this.motorPoses.putAll(motorPoses);
        this.motorPowers.clear();
        this.motorPowers.putAll(motorPowers);
    }

    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(long start, Map<T, Double> motorPoses, Map<DcMotorExHandler, Double> motorPowers) {
        this.start = start;
        this.motorPoses.clear();
        this.motorPoses.putAll(motorPoses);
        this.motorPowers.clear();
        this.motorPowers.putAll(motorPowers);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(Map<T, Double> motorPoses, Map<DcMotorExHandler, Double> motorPowers) {
        this(System.nanoTime(), motorPoses, motorPowers);
    }

    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(long start, Map<T, Double> motorPoses, boolean getPower) {
        this(start, motorPoses, motorPoses.entrySet().stream()
                    .filter(e -> e.getKey() instanceof DcMotorExHandler)
                    .collect(Collectors.toMap(
                        e -> (DcMotorExHandler) e.getKey(),
                        e -> getPower ? 1d : ((DcMotorExHandler) e.getKey()).getPower()
                    ))
        );
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(long start, Map<T, Double> motorPoses) {
        this(start, motorPoses, false);
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(Map<T, Double> motorPoses) {
        this(System.nanoTime(), motorPoses);
    }

    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(long start, List<T> motors) {
        this.start = start;
        motors.forEach(motor -> {
            motorPoses.put(motor, motor.getPosition());
            if (motor instanceof DcMotorExHandler) {
                motorPowers.put((DcMotorExHandler) motor, ((DcMotorExHandler) motor).getPower());
            }
        });
    }
    public <T extends HardwareComponentHandler<?>> HardwareSnapshot(List<T> motors) {
        this(System.nanoTime(), motors);
    }



    public HardwareSnapshot offset(long offset) {
        this.start = start - offset;
        this.offset = offset;
        return this;
    }
    public void end(long end) {
        this.end = end;
    }
    public void end() {
        end(System.nanoTime() - offset);
    }

    // Accepts raw system times/offsetted times
    public boolean happened(long time) {
        return time >= start && time <= end;
    }

    public void recreate() {
        Map<DcMotorExHandler, Double> herePowers = new HashMap<>(motorPowers);
        motorPoses.forEach((k, v) -> {
            if (k instanceof DcMotorExHandler) {
                DcMotorExHandler kD = (DcMotorExHandler) k;
                herePowers.remove(k);
                kD.setPosition(v, motorPowers.get(kD));
            } else {
                k.setPosition(v);
            }
        });
        herePowers.forEach(DcMotorExHandler::setPower);
    }




    private <T extends HardwareComponentHandler<?>> String serializeMap(Map<T, Double> map) {
        return map.entrySet().stream().map(e -> e.getKey().getName() + ": " + e.getValue()).collect(Collectors.joining(", "));
    }

    public String serialize() {
        return "[(" + start + ", " + end + (offset != 0L ? ", " + offset : "") + "), {" + serializeMap(motorPoses) + "}, {" + serializeMap(motorPowers) + "}]";
    }
    public static HardwareSnapshot deserialize(String serialized) {
        Pattern serialWrapper = Pattern.compile("\\[\\((\\d+), (\\d+)\\), \\{(.*?)\\}, \\{(.*?)\\}]");
        Pattern serialMap = Pattern.compile("([^:]+): ([^,]+)");

        Matcher mainMatcher = serialWrapper.matcher(serialized);
        if (!mainMatcher.matches()) {
            throw new IllegalArgumentException("Invalid serialized string format");
        }

        long start = Long.parseLong(Objects.requireNonNull(mainMatcher.group(1)));
        long end = Long.parseLong(Objects.requireNonNull(mainMatcher.group(2)));
        long offset = Long.parseLong(Optional.ofNullable(mainMatcher.group(3)).orElse("0"));
        String motorPosesStr = mainMatcher.group(3);
        String motorPowersStr = mainMatcher.group(4);
        if (motorPosesStr == null && motorPowersStr == null) {
            throw new IllegalArgumentException("No maps specified");
        }

        Map<HardwareComponentHandler<?>, Double> motorPoses = new HashMap<>();
        if (motorPosesStr != null) {
            Matcher motorPosesMatcher = serialMap.matcher(motorPosesStr);
            while (motorPosesMatcher.find()) {
                motorPoses.put(HandlerMap.get(motorPosesMatcher.group(1)),
                        Double.parseDouble(Objects.requireNonNull(motorPosesMatcher.group(2))));
            }
        }

        Map<DcMotorExHandler, Double> motorPowers = new HashMap<>();
        if (motorPowersStr != null) {
            Matcher motorPowersMatcher = serialMap.matcher(motorPowersStr);
            while (motorPowersMatcher.find()) {
                motorPowers.put(HandlerMap.get(motorPowersMatcher.group(1)),
                        Double.parseDouble(Objects.requireNonNull(motorPowersMatcher.group(2))));
            }
        }

        HardwareSnapshot snapshot = new HardwareSnapshot(start, end, offset, motorPoses, motorPowers);
        snapshot.motorPoses.putAll(motorPoses);
        snapshot.motorPowers.putAll(motorPowers);
        return snapshot;
    }


    public boolean equals(HardwareSnapshot state) {
        return !(state.motorPoses.entrySet().stream().anyMatch(e -> !Objects.equals(motorPoses.get(e.getKey()), e.getValue())) ||
                state.motorPowers.entrySet().stream().anyMatch(e -> !Objects.equals(motorPowers.get(e.getKey()), e.getValue())));
    }

}
