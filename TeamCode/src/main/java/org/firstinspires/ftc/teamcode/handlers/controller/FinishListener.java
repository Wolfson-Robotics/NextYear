package org.firstinspires.ftc.teamcode.handlers.controller;

import java.util.function.Supplier;

public class FinishListener extends ControllerListener {

    private boolean fired = false;
    private boolean waited = false;

    private FinishListener(Supplier<Boolean> control, Runnable fn) {
        super(control, fn);
    }

    public void update() {
        if (control.get()) {
            fired = true;
            return;
        }
        if (fired) {
            if (!waited) {
                waited = true;
                try {
                    Thread.sleep(400);
                } catch (Exception e) {}
            } else {
                fn.run();
                waited = false;
            }
        }
        fired = false;
    }

    public static FinishListener of(Supplier<Boolean> control, Runnable fn) {
        return new FinishListener(control, fn);
    }
}
