package org.firstinspires.ftc.teamcode.handlers.controller;

import java.util.function.Supplier;

public class InitListener extends ControllerListener {

    private boolean fired = false;

    private InitListener(Supplier<Boolean> control, Runnable fn) {
        super(control, fn);
    }

    public void update() {
        if (control.get()) {
            if (!fired) {
                fired = true;
                fn.run();
            }
        } else {
            fired = false;
        }
    }

    public static InitListener of(Supplier<Boolean> control, Runnable fn) {
        return new InitListener(control, fn);
    }

}
