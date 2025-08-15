package org.firstinspires.ftc.teamcode.auto.debug;

import com.qualcomm.robotcore.hardware.VoltageSensor;

import java.util.function.Supplier;

public class HardwareContext {

    private HardwareSnapshot prev;
    private double prevVoltage;

    private VoltageSensor voltageSensor;
    private Supplier<VoltageSensor> voltageSensorFn;
    private double lastVoltage;

    private long offset;

    public HardwareContext(HardwareSnapshot prev, VoltageSensor voltageSensor) {
        this.prev = prev;
        this.prevVoltage = prev != null ? prev.voltage() : HardwareSnapshot.NO_VOLTAGE;
        this.voltageSensor = voltageSensor;
        this.lastVoltage = voltageSensor != null ? voltageSensor.getVoltage() : HardwareSnapshot.NO_VOLTAGE;
    }
    public HardwareContext(HardwareSnapshot prev) {
        this(prev, null);
    }
    public HardwareContext(VoltageSensor voltageSensor) {
        this(null, voltageSensor);
    }
    public HardwareContext(Supplier<VoltageSensor> voltageSensorFn) {
        this(null, voltageSensorFn.get());
        this.voltageSensorFn = voltageSensorFn;
    }
    public HardwareContext() {
        this(null, null);
    }

    public HardwareSnapshot prev() {
        return prev;
    }
    public double prevVoltage() {
        return this.prevVoltage;
    }
    private double voltageFromSensor(VoltageSensor sensor) {
        if (sensor == null) return lastVoltage;
        try {
            return sensor.getVoltage();
        } catch (Exception e) { return lastVoltage; }
    }
    public double voltage() {
        double dVoltage = voltageFromSensor(voltageSensor);
        return (this.lastVoltage = (dVoltage == -1 ? voltageFromSensor(this.voltageSensor = voltageSensorFn.get()) : dVoltage));
    }
    public long offset() {
        return offset;
    }

    public void prev(HardwareSnapshot prev) {
        this.prev = prev;
        this.prevVoltage = prev.voltage();
    }
    public void voltage(Supplier<VoltageSensor> voltageSensorFn) {
        this.voltageSensorFn = voltageSensorFn;
    }
    public void voltage(VoltageSensor voltageSensor) {
        this.voltageSensor = voltageSensor;
    }
    public void voltage(double voltage) {
        this.lastVoltage = voltage;
    }
    public void offset(long time) {
        offset += time;
    }

    public boolean hasPrev() {
        return this.prev != null;
    }
    public boolean hasVoltage() {
        return this.voltageSensor != null || this.lastVoltage != -1 || (this.voltageSensorFn != null && (this.voltageSensor = this.voltageSensorFn.get()) != null);
    }

    public static HardwareContext empty() {
        return new HardwareContext();
    }


}
