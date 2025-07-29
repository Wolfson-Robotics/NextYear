package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.handlers.camera.OpenCvCameraHandler;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.wolfsonrobotics.RobotWebServer.server.RobotWebServer;
import org.wolfsonrobotics.RobotWebServer.server.ServerOpMode;

import java.io.IOException;

public class RobotWithServer extends RobotBase implements ServerOpMode {

    RobotWebServer server;

    @Override
    public void init() {
        super.init();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        this.camera = new OpenCvCameraHandler(camera);
        server = new RobotWebServer(this);
        try {
            server.start();
        } catch (IOException e) {
            server.stop();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        server.stop();
        camera.closeCamera();
    }

    @Override
    public void loop() {
        telemetry.addLine("Test!");
    }


    @Override
    public String[] getExcludedMethods() {
        return new String[] {"init", "loop", "stop", "registerHandlers"};
    }

    @Override
    public Mat getCameraFeed() {
       return camera.getCurrentFrame();
    }
}
