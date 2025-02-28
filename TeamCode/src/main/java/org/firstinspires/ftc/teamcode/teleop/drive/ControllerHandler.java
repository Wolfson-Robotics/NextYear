package org.firstinspires.ftc.teamcode.teleop.drive;


import org.firstinspires.ftc.teamcode.handler.HardwareComponentHandler;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ControllerHandler {

    private final HardwareComponentHandler device;
    private final Runnable control;

    private final Preset[] presets;

    private final List<Preset> lastPresets = new ArrayList<>();


    public ControllerHandler(HardwareComponentHandler device,
                             Runnable control,
                             Preset... presets) {
        this.device = device;
        this.control = control;
        this.presets = presets;
    }
    public ControllerHandler(HardwareComponentHandler device, Preset... presets) {
        this.device = device;
        this.control = () -> {};
        this.presets = presets;
    }


    public void handle() {
        List<Double> poses = new ArrayList<>();
        for (Preset preset : presets) {
            if (preset.canExec() && !lastPresets.contains(preset)) {
                lastPresets.add(preset);
                poses.add(preset.pos());
            } else {
                lastPresets.remove(preset);
            }
        }
        if (poses.size() == 1) {
            this.device.setPosition(poses.get(0));
        }
        this.control.run();
    }

}
