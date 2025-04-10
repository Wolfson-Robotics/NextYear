package org.firstinspires.ftc.teamcode.handlers.controller;

import java.util.function.Supplier;

public abstract class ControllerListener {

    protected final Supplier<Boolean> control;
    protected final Runnable fn;

    protected ControllerListener(Supplier<Boolean> control, Runnable fn) {
        this.control = control;
        this.fn = fn;
    }
    abstract public void update();
}
