package org.firstinspires.ftc.teamcode.teleop.drive;

import java.util.function.Supplier;

public class Preset {

    private final Supplier<Boolean> cond;
    private final double pos;

    private Preset(Supplier<Boolean> cond, double pos) {
        this.cond = cond;
        this.pos = pos;
    }
    public static Preset of(Supplier<Boolean> cond, double pos) {
        return new Preset(cond, pos);
    }

    public boolean canExec() {
        return this.cond.get();
    }

    public double pos() {
        return this.pos;
    }
}
