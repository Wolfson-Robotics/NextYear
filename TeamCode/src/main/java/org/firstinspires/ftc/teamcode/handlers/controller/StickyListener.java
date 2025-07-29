package org.firstinspires.ftc.teamcode.handlers.controller;

import java.util.function.Supplier;

public class StickyListener extends ControllerListener {

    private boolean on = false, switched = false;
    private final Runnable onFn, offFn;

    private StickyListener(Supplier<Boolean> control, Runnable onFn, Runnable offFn) {
        super(control, onFn);
        this.onFn = onFn;
        this.offFn = offFn;
    }

    public void update() {
        if (control.get()) {
            if (!on) {
                on = true;
                if (switched) {
                    switched = false;
                    offFn.run();
                } else {
                    switched = true;
                    onFn.run();
                }
            }
        } else if (on) {
            on = false;
        }
    }

    public static StickyListener of(Supplier<Boolean> control, Runnable onFn, Runnable offFn) {
        return new StickyListener(control, onFn, offFn);
    }

}
