package org.firstinspires.ftc.teamcode.oldcodebase.auto.colorsample;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.oldcodebase.auto.AutoJava;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.List;
import java.util.TreeMap;

@Autonomous(name = "ColorSampleDetectionProto", group = "Auto")
public class ColorSampleDetectionProto extends AutoJava {

    private ColorFilterPipeline pipeline = null;

    public ColorSampleDetectionProto() {
        super(true);
    }

    @Override
    public void initCamera() {

//        double[] lowerYellow = { 119, 56, 0 };
//        double[] upperYellow = {242, 209, 100 };
        double[] lowerYellow = {35, 70, 70};
        double[] upperYellow = {55, 100, 100};
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        this.camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, webcamName), cameraMonitorViewId);
//        this.pipeline = new ColorFilterPipeline(new double[] { 35, 70, 70 }, new double[] { 55, 100, 100 });
        this.pipeline = new ColorFilterPipeline(lowerYellow, upperYellow);
        camera.setPipeline(pipeline);


        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(432,240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera error code:", errorCode);
                telemetry.update();
            }
        });

    }

    @Override
    public void runOpMode() {
        this.initCamera();
        this.commonAutoInit();

        sleep(3000);
        camera.closeCameraDevice();

        TreeMap<Integer, List<Integer>> colorLocs = this.pipeline.getColorLocs();
        double degrees = 0;
        try {
            degrees = ColorSampleDetectionPipeline.processFrame(colorLocs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        telemetry.addData("anchorpts size: ", ColorSampleDetectionPipeline.getSize());
        telemetry.addData("degrees: ", degrees);
        telemetry.update();

        turnBot(-degrees);
        sleep(60000);


    }
}
