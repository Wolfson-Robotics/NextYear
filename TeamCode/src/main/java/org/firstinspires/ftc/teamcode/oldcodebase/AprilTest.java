package org.firstinspires.ftc.teamcode.oldcodebase;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.oldcodebase.auto.AutoJava;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Autonomous(name = "April Tag Test", group = "Auto")
public class AprilTest extends AutoJava
{
    AprilTagProcessor tagProcessor;

    protected AprilTest(boolean blue) {
        super(blue);
    }


    @Override
    public void runOpMode() {
        initMotors();
        tagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setDrawTagID(true)
                .build();
        VisionPortal visionPortal = new VisionPortal.Builder()
                .addProcessor(tagProcessor)
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                . setCameraResolution(new Size(640,480))
                .enableLiveView(true)
                .build();
       /* while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING);


        ExposureControl exposure = visionPortal.getCameraControl(ExposureControl.class);
        exposure.setMode(ExposureControl.Mode.Manual);
        exposure.setExposure(15, TimeUnit.MILLISECONDS);

        GainControl gain = visionPortal.getCameraControl(GainControl.class);
        gain.setGain(255);*/


        waitForStart();
        boolean start = false;
        while (opModeIsActive()) {
            if(tagProcessor.getDetections().size() > 0)
            {
                AprilTagDetection tag = tagProcessor.getDetections().get(0);
                this.ftcPose = tag.ftcPose;
                telemetry.addLine(String.format("XYZ %6.2f %6.2f %6.2f", this.ftcPose.x, this.ftcPose.y, this.ftcPose.z));
                telemetry.addData("bearing", this.ftcPose.bearing);
                telemetry.addData("yaw", this.ftcPose.yaw);
                telemetry.addData("roll", this.ftcPose.roll);
                telemetry.addData("range", this.ftcPose.range);
                telemetry.addData("pitch", this.ftcPose.pitch);
                telemetry.addData("elevation", this.ftcPose.elevation);
                telemetry.update();
                sleep(2000);
                if(!start && tag != null && tagProcessor != null)
                {
                    start = true;
                    fixBot(1.03f, 22, -4);
                }
                telemetry.addLine(String.format("XYZ %6.2f %6.2f %6.2f", this.ftcPose.x, this.ftcPose.y, this.ftcPose.z));
                telemetry.addData("bearing", this.ftcPose.bearing);
                telemetry.addData("yaw", this.ftcPose.yaw);
                telemetry.addData("roll", this.ftcPose.roll);
                telemetry.addData("range", this.ftcPose.range);
                telemetry.addData("pitch", this.ftcPose.pitch);
                telemetry.addData("elevation", this.ftcPose.elevation);
                //telemetry.update();



            }
        }

    }

    AprilTagPoseFtc ftcPose;

    protected void fixBot(float TargetX, float TargetY, float TargetYaw)
    {
        AprilTagDetection tag = tagProcessor.getDetections().get(0);
        int PosNeg;
        //test the chat gpt code
        double yawError = TargetYaw - tag.ftcPose.yaw;
//        double yawTolerance = 4;  // Define a small tolerance for yaw
        double yawTolerance = Math.abs(TargetYaw - Math.floor(yawError));
//        double yawTolerance = 4;
        double pivotPower;
        telemetry.addLine("initial yaw: " + tag.ftcPose.yaw);
        telemetry.addLine("target yaw: " + TargetYaw);
        telemetry.addLine("yaw error: " + yawError);
        telemetry.addLine("yaw tolerance: " + yawTolerance);
        telemetry.update();
        sleep(2000);

        int whileIterations = 0;
        double lastYawError = 0;
        double avgRateOfChange = 0;
        int avgIterations = 1;
        boolean flaggedPivotPow = false;
        while (Math.abs(yawError) > Math.abs(TargetYaw)) {
            whileIterations++;
            if (tagProcessor.getDetections().isEmpty()) {
                removePower();
/*
                lastYawError = yawError;
                avgRateOfChange = lastYawError / whileIterations;
                telemetry.addLine("current yaw: " + tag.ftcPose.yaw);
                telemetry.addLine("target yaw: " + TargetYaw);
                telemetry.addLine("yaw error: " + yawError);
                telemetry.addLine("yaw tolerance: " + yawTolerance);
                telemetry.addLine("while iterations: " + whileIterations);
                telemetry.addLine("avgrateofchange: " + avgRateOfChange);
                telemetry.update();
                sleep(3000);*/
                break;
            }
            tag = tagProcessor.getDetections().get(0);
            this.ftcPose = tag.ftcPose;
            PosNeg = (this.ftcPose.yaw < 0 ? 1 : -1);
            // Update yaw error
            if (avgRateOfChange != 0) {
                yawError = TargetYaw - (this.ftcPose.yaw + (PosNeg*(avgRateOfChange * avgIterations)));
                avgIterations++;
                telemetry.addLine("new yaw error: " + yawError);
                telemetry.addLine("new avg iterations: " + avgIterations);
                telemetry.update();
            } else {
                yawError = TargetYaw - this.ftcPose.yaw;
            }


//            telemetry

            // Proportional control to smooth the yaw adjustment
            double kP = 1.55;  // Proportional control constant (adjust this for better results)
            if (yawError < 0 && PosNeg < 0) {
                pivotPower = (kP * yawError);
            } else {
                pivotPower = (kP * yawError) * PosNeg;
            }
            telemetry.addData("posNeg: ", PosNeg);
            telemetry.addData("yawError", yawError);
            telemetry.addData("current yaw: ", tag.ftcPose.yaw);
            telemetry.addData("yawTolerance: ", yawTolerance);
            telemetry.addData("pivotPower before math.min: ", pivotPower);

            // Ensure the power stays within motor limits [-1, 1]
            pivotPower = Math.max(-1, Math.min(1, pivotPower));
            telemetry.addData("pivotPower: ", pivotPower);
            telemetry.update();

            double aP = 0.7;
            pivotPower = aP * pivotPower;

            // Set motor powers for turning the robot
            removePower();

        }
        removePower();
        /*
        double pivot;
        while (Math.abs(TargetYaw-tag.ftcPose.yaw) > 1)
        {
            if(tagProcessor.getDetections().size() > 0) {
                tag = tagProcessor.getDetections().get(0);
                PosNeg = (tag.ftcPose.yaw < 0) ? 1 : -1;
                pivot = (Math.abs(Math.abs(tag.ftcPose.yaw)-Math.abs(TargetYaw))/90)*PosNeg;
                right_drive1.setPower(-pivot);
                right_drive2.setPower(-pivot);
                left_drive1.setPower(pivot);
                left_drive2.setPower(pivot);
            }
        }*/
        sleep(1500);
//        tag = tagProcessor.getDetections().get(0);
        PosNeg = (this.ftcPose.x > 0) ? 1 : -1;
        double InitialX = this.ftcPose.x;
        double horizontal;
        boolean pitchFlagged  = false;
        while (Math.abs(Math.abs(TargetX)-Math.abs(this.ftcPose.x)) > 0.2)
        {
            if(tagProcessor.getDetections().size() > 0) {
                if (!pitchFlagged) {
                    pitchFlagged = true;
                }
                tag = tagProcessor.getDetections().get(0);
                this.ftcPose = tag.ftcPose;
                horizontal = (Math.abs(tag.ftcPose.x / InitialX) * PosNeg) * 0.6;
                rf_drive.setPower(-horizontal);
                rb_drive.setPower(horizontal);
                lf_drive.setPower(horizontal);
                lb_drive.setPower(-horizontal);
            } else {
                if (pitchFlagged) {
                    removePower();
                    break;
                }
                horizontal = (Math.abs(this.ftcPose.x / InitialX) * PosNeg) * 0.6;
                rf_drive.setPower(-horizontal);
                rb_drive.setPower(horizontal);
                lf_drive.setPower(horizontal);
                lb_drive.setPower(-horizontal);
            }
        }
        sleep(1500);
//        tag = tagProcessor.getDetections().get(0);
        PosNeg = (TargetY > this.ftcPose.y) ? -1 : 1;
        double InitialY = this.ftcPose.y;
        double vertical;
        boolean rollFlagged = false;
        while (Math.abs(TargetY-this.ftcPose.y) > 0.2)
        {
            if(tagProcessor.getDetections().size() > 0) {
                if (!rollFlagged) {
                    rollFlagged = true;
                }
                tag = tagProcessor.getDetections().get(0);
                this.ftcPose = tag.ftcPose;
                vertical = ((tag.ftcPose.y / InitialY) * PosNeg) * 0.6;
                rf_drive.setPower(vertical);
                rb_drive.setPower(vertical);
                lf_drive.setPower(vertical);
                lb_drive.setPower(vertical);
            } else {
                if (rollFlagged) {
                    removePower();
                    break;
                }
                vertical = ((this.ftcPose.y / InitialY) * PosNeg) * 0.6;
                rf_drive.setPower(vertical);
                rb_drive.setPower(vertical);
                lf_drive.setPower(vertical);
                lb_drive.setPower(vertical);
            }
        }
        removePower();
    }



}
