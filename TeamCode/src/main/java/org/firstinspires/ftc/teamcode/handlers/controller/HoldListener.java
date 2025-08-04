package org.firstinspires.ftc.teamcode.handlers.controller;

import java.util.function.Supplier;

public class HoldListener extends ControllerListener {

    private boolean on = false;
    private final Runnable offFn;

    private HoldListener(Supplier<Boolean> control, Runnable onFn, Runnable offFn) {
        super(control, onFn);
        this.offFn = offFn;
    }
    private HoldListener(Supplier<Boolean> control, Runnable onFn) {
        this(control, onFn, () -> {});
    }

    public void update() {
        if (control.get()) {
            if (!on) {
                on = true;
                fn.run();
            }
        } else if (on) {
            on = false;
            offFn.run();
        }
    }

    public static HoldListener of(Supplier<Boolean> control, Runnable onFn, Runnable offFn) {
        return new HoldListener(control, onFn, offFn);
    }
    public static HoldListener of(Supplier<Boolean> control, Runnable onFn) {
        return new HoldListener(control, onFn);
    }

}
