package org.firstinspires.ftc.teamcode.handlers.camera;

import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.opencv.core.Mat;

//Can't implement HardwareComponentHandler due to type restrictions, so it might be worth adding an ever lower HandlerClass that all Handlers can extend
public abstract class CameraHandler<T extends CameraStreamSource> {

    protected final T device;

    public CameraHandler(T device) {
        this.device = device;
    }

    public abstract void closeCamera();

    protected Mat current_frame;
    public abstract Mat getCurrentFrame();

}
