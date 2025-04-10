package org.firstinspires.ftc.teamcode.handlers.controller;

import java.util.function.Supplier;

public class ToggleListener extends ControllerListener {

    private boolean on = false;
    private final Runnable offFn;

    private ToggleListener(Supplier<Boolean> control, Runnable onFn, Runnable offFn) {
        super(control, onFn);
        this.offFn = offFn;
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

    public static ToggleListener of(Supplier<Boolean> control, Runnable onFn, Runnable offFn) {
        return new ToggleListener(control, onFn, offFn);
    }

}
