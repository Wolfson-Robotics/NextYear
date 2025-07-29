package org.firstinspires.ftc.teamcode.handlers.camera;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

public class OpenCvCameraHandler extends CameraHandler<OpenCvCamera> {

    Mat current_frame;

    public OpenCvCameraHandler(OpenCvCamera device) {
        super(device);

        // TODO: Add an option to set a pipeline (when needed)
        //this.pixelDetection = new PixelDetection(this.blue);
        //camera.setPipeline(pixelDetection);

        device.setPipeline(new OpenCvPipeline() {
            @Override
            public Mat processFrame(Mat input) {
                current_frame = input;
                return input;
            }
        });

        device.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened() {
                device.startStreaming(432,240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera error code:", errorCode);
                telemetry.update();
            }
        });
    }

    @Override
    public void closeCamera() {
        device.closeCameraDevice();
    }

    @Override
    public Mat getCurrentFrame() {
        return current_frame;
    }

}
