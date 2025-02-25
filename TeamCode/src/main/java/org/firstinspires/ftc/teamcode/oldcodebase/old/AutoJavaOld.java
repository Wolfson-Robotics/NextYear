package org.firstinspires.ftc.teamcode.oldcodebase.old;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;


public abstract class AutoJavaOld extends LinearOpMode {


    protected DcMotorEx right_drive1;
    protected DcMotorEx right_drive2;
    protected DcMotorEx left_drive1;
    protected DcMotorEx left_drive2;


    protected final double WHEEL_DIAMETER_INCHES = 3.0;
    protected final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER_INCHES;
    protected final double TICKS_PER_ROTATION = 288;
    protected final double intCon = 8.727272;


    protected ArrayList<String> movements = new ArrayList<>();
    //protected volatile SleeveDetection.ParkingPosition pos;

    protected PixelDetection pixelDetection;
    protected OpenCvCamera camera;

    // Name of the Webcam to be set in the config
    protected String webcamName = "Webcam 1";


    protected double powerFactor = 1;
    protected boolean blue = false;


    protected AutoJavaOld(boolean blue) {
        this.blue = blue;
    }




    protected void initMotors() {

        right_drive1 = hardwareMap.get(DcMotorEx.class, "right_drive1");
        right_drive2 = hardwareMap.get(DcMotorEx.class, "right_drive2");
        left_drive1 = hardwareMap.get(DcMotorEx.class, "left_drive1");
        left_drive2 = hardwareMap.get(DcMotorEx.class, "left_drive2");

        right_drive1.setDirection(DcMotorSimple.Direction.REVERSE);
        right_drive2.setDirection(DcMotorSimple.Direction.REVERSE);


        powerFactor = 1;

    }



    protected void initCamera() {

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        this.camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, webcamName), cameraMonitorViewId);
        this.pixelDetection = new PixelDetection(this.blue);
        camera.setPipeline(pixelDetection);


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


    protected void moveBot(double distIN, float vertical, float pivot, float horizontal) {

        // 23 motor tics = 1 IN
        int motorTics;
        int posNeg = (vertical >= 0) ? 1 : -1;

        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));

        if (horizontal >= 0) {
            motorTics = left_drive1.getCurrentPosition() + (int) ((distIN * intCon) * posNeg);
            if (posNeg == -1) {
                while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    idle();
                }
            } else {
                while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    idle();
                }
            }
        } else {
            motorTics = right_drive1.getCurrentPosition() + (int) ((distIN * intCon) * posNeg);
            while ((right_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                idle();
            }
        }
        right_drive1.setPower(0);
        left_drive1.setPower(0);
        right_drive2.setPower(0);
        left_drive2.setPower(0);

    }

    protected void moveBotTics(double motorTics, float vertical, float pivot, float horizontal) {

        int posNeg = (vertical >= 0) ? 1 : -1;

        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));

        if (horizontal >= 0) {
            motorTics = left_drive1.getCurrentPosition() + (int) ((motorTics) * posNeg);
            if (posNeg == -1) {
                while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    idle();
                }
            } else {
                while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    idle();
                }
            }
        } else {
            motorTics = right_drive1.getCurrentPosition() + (int) ((motorTics) * posNeg);
            while ((right_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                idle();
            }
        }
        right_drive1.setPower(0);
        left_drive1.setPower(0);
        right_drive2.setPower(0);
        left_drive2.setPower(0);

    }

    protected void turnBot(int degrees) {
        // 13.62 inches is default robot length
        double robotLength = 13.62;
        double distUnit = (robotLength) / (Math.cos(45));
        double distIN = Math.abs((distUnit * ((degrees*1.75))) / 90);
        int motorTics;
        int pivot = (degrees >= 0) ? 1 : -1;
        right_drive1.setPower(powerFactor * (-pivot));
        right_drive2.setPower(powerFactor * (-pivot));
        left_drive1.setPower(powerFactor * (pivot));
        left_drive2.setPower(powerFactor * (pivot));
        motorTics = left_drive1.getCurrentPosition() + (int) Math.round((distIN * intCon)* pivot);
        if(pivot == 1) {
            while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                idle();
            }
        }
        if(pivot == -1) {
            while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                idle();
            }
        }
        right_drive1.setPower(0);
        left_drive1.setPower(0);
        right_drive2.setPower(0);
        left_drive2.setPower(0);

    }

    protected void moveServo(Servo servo, double targetPosition, long speed) {
        if (Math.abs(servo.getPosition() - targetPosition) > 0.01) {
            // Move the servo towards the target position slowly
            if (servo.getPosition() < targetPosition) {
                servo.setPosition(servo.getPosition() + .01);
            } else {
                servo.setPosition(servo.getPosition() - .01);
            }

            // Sleep for a short duration (adjust as needed)
            sleep(speed); // Sleep for 100 milliseconds (adjust for desired speed)
        }

    }











    public abstract void runOpMode();

}


