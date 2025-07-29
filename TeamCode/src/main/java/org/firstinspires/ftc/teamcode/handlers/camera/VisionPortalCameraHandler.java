package org.firstinspires.ftc.teamcode.handlers.camera;

import android.graphics.Canvas;
import android.util.Size;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Mat;

import java.util.Arrays;
import java.util.stream.Collectors;

public class VisionPortalCameraHandler extends CameraHandler<VisionPortal> {

    public VisionPortalCameraHandler(VisionPortal device) {
        super(device);
    }

    @Override
    public void closeCamera() {
        device.close();
    }

    @Override
    public Mat getCurrentFrame() {
        return null; //TODO: figure out how tf to do this
    }




    //https://ftc-docs.firstinspires.org/en/latest/apriltag/vision_portal/visionportal_init/visionportal-init.html
    public static VisionPortal createCustomVisionPortal(CameraName camera, VisionProcessor... processor) {
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(camera);
        builder.setCameraResolution(new Size(432, 240));
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG); // MJPEG offers more frames, YUY2 offers better quality
        builder.enableLiveView(true);
        builder.setAutoStopLiveView(true);
        builder.setShowStatsOverlay(true); //Seems interesting TODO: remove or not

        //Add VisionProcessors
        Arrays.stream(processor).collect(Collectors.toList())
                .forEach(builder::addProcessor);

        return builder.build();
    }

}
