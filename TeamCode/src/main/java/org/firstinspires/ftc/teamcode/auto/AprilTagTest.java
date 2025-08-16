package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.handlers.camera.VisionPortalCameraHandler;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

//github.com/Wolfson-Robotics/NextYear/blob/main/FtcRobotController/src/main/java/org/firstinspires/ftc/robotcontroller/external/samples/RobotAutoDriveToAprilTagOmni.java
@Autonomous(name = "AprilTagTest", group = "Auto")
public class AprilTagTest extends RobotBase {

    VisionPortal vp;
    AprilTagProcessor aTagProc;


    final double DESIRED_DISTANCE_IN = 12.0; // How close the camera should get in inches


    @Override
    public void init() {
        super.init();

//        aTagProc = AprilTagProcessor.easyCreateWithDefaults();
        aTagProc = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setDrawTagID(true)
                .build();
        aTagProc.setDecimation(2);

        vp = VisionPortalCameraHandler.createCustomVisionPortal(
            hardwareMap.get(WebcamName.class, "Webcam 1"),
            aTagProc
        );
        camera = new VisionPortalCameraHandler(vp);
    }

    @Override
    public void stop() {
        super.stop();
        camera.closeCamera();
    }


    final int DESIRED_TAG_ID = 1;
    @Override
    public void loop() {
//        for (AprilTagDetection detection : aTagProc.getDetections()) {
//            if (detection.id == DESIRED_TAG_ID) {
//                detectedTag = detection/;
//                break;
//            }
//        }
        List<AprilTagDetection> detections = aTagProc.getDetections();
        if (detections.isEmpty()) {
            return;
        }
        AprilTagDetection detectedTag = detections.get(0);

        telemetry.addData("Found", "ID %d (%s)", detectedTag.id, detectedTag.metadata.name);
        telemetry.addData("Range",  "%5.1f inches", detectedTag.ftcPose.range);
        telemetry.addData("Bearing","%3.0f degrees", detectedTag.ftcPose.bearing);
        telemetry.addData("Yaw","%3.0f degrees", detectedTag.ftcPose.yaw);

        float drive   = (float) (detectedTag.ftcPose.range - DESIRED_DISTANCE_IN);
        float turn    = (float) detectedTag.ftcPose.bearing;
        float strafe  = (float) detectedTag.ftcPose.yaw;

        moveBot(drive, turn, strafe);
    }

}
