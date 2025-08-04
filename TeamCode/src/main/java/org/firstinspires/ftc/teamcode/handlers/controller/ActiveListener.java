package org.firstinspires.ftc.teamcode.handlers.controller;

import java.util.function.Supplier;

public class ActiveListener extends ControllerListener {

    private ActiveListener(Supplier<Boolean> control, Runnable fn) {
        super(control, fn);
    }

    public void update() {
        if (control.get()) fn.run();
    }

    public static ActiveListener of(Supplier<Boolean> control, Runnable fn) {
        return new ActiveListener(control, fn);
    }

}
