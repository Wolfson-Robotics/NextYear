package org.firstinspires.ftc.teamcode.oldcodebase;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public abstract class RobotBase extends LinearOpMode {

    protected DcMotorEx rf_drive;
    protected DcMotorEx rb_drive;
    protected DcMotorEx lf_drive;
    protected DcMotorEx lb_drive;

    protected DcMotorEx lift;

    protected Servo arm;
    protected Servo claw;

    // Second intake servos
    protected DcMotorEx slide1;
    protected DcMotorEx slide2;
    protected Servo slideArm;
    protected CRServo leftRoller;
    protected CRServo rightRoller;


    protected OpenCvCamera camera;
    protected String webcamName = "Webcam 1";

    protected Rev2mDistanceSensor distanceSensor;

    protected double powerFactor = 1;

    protected final double WHEEL_DIAMETER_INCHES = 3.0;
    protected final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER_INCHES;
    protected final double TICKS_PER_ROTATION = 288;

    protected final double intCon = 8.727272;
    protected final double ticsPerInch = 6.1397574560909;
    // constant is 230/90 (since regular turnBot(230) is 90 degrees)
    protected final double degConv = 2.5555555555555555555555555555556;

    protected final double closedClaw = 0.46;
    protected final double openClaw = 0.30;

    
    // drive constants
    protected final double manualArmSpeed = 0.01;
    
    protected final double liftStationaryPower = 0.05;
    protected final int liftRangeTolerance = 6;
    protected final double liftDriveLim = 0.775;

    protected final int minLift = -5;
    protected final int maxLift = -4165;

    // slide
    protected final int slideRangeTolerance = 7;
    // max slide is the furthest, min slide is the cloest
//    protected final int maxSlide = -1826;
//    protected final int minSlide = -1772;
    protected final int maxSlide = -36;
    protected final int minSlide = 0;
    protected final double minSlideArm = 1;
    protected final double maxSlideArm = -0.6;

//    protected final double maxArm = 0.316;
//    protected final double minArm = 0;
//    protected final double maxArm = 1;
//    protected final double minArm = 0.7;
    protected final double minArm = 0.75;
    protected final double maxArm = 1;


    protected final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);



    protected void initMotors() {

        rf_drive = hardwareMap.get(DcMotorEx.class, "right_drive1");
        rb_drive = hardwareMap.get(DcMotorEx.class, "right_drive2");
        lf_drive = hardwareMap.get(DcMotorEx.class, "left_drive1");
        lb_drive = hardwareMap.get(DcMotorEx.class, "left_drive2");

        lf_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        lb_drive.setDirection(DcMotorSimple.Direction.REVERSE);

        lift = hardwareMap.get(DcMotorEx.class, "lift");
        arm = hardwareMap.get(Servo.class, "arm");
        claw = hardwareMap.get(Servo.class, "claw");

        slide1 = hardwareMap.get(DcMotorEx.class, "slide");
        slide2 = hardwareMap.get(DcMotorEx.class, "slide2");
        slideArm = hardwareMap.get(Servo.class, "slidearm");
        slideArm.setDirection(Servo.Direction.REVERSE);

        leftRoller = hardwareMap.get(CRServo.class, "leftroller");
        rightRoller = hardwareMap.get(CRServo.class, "rightroller");

        claw.setPosition(this.closedClaw);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setDirection(DcMotorSimple.Direction.REVERSE);

        slide1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        arm.setPosition(this.minArm);

    }
    public void setBrakeMotors() {
        rf_drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb_drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lf_drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb_drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }


    protected void initCamera() {

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        this.camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, webcamName), cameraMonitorViewId);
//        this.pixelDetection = new PixelDetection(this.blue);
//        camera.setPipeline(pixelDetection);


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

    protected void moveServoSpeed(Servo servo, double targetPosition, long speed) {
        if (Math.abs(servo.getPosition() - targetPosition) > 0.01) {
            // Move the servo towards the target position slowly
            if (servo.getPosition() < targetPosition) {
                servo.setPosition(servo.getPosition() + speed);
            } else {
                servo.setPosition(servo.getPosition() - speed);
            }

            // Sleep for a short duration (adjust as needed)
            sleep(100); // Sleep for 100 milliseconds (adjust for desired speed)
        }

    }
    protected void moveServo(Servo servo, double targetPosition) {
        /*
        while (Math.abs(servo.getPosition() - targetPosition) > 0.01) {
            if (servo.getPosition() < targetPosition) {
                servo.setPosition(targetPosition);
            } else {
                servo.setPosition(targetPosition);
            }
            sleep(50);
        }*/
        servo.setPosition(targetPosition);
        while (Math.abs(servo.getPosition() - targetPosition) > 0.0001) {
            idle();
        }
    }

    protected void moveMotor(DcMotor motor, int targetPosition, double speed, boolean stay) {
        int oldTargetPosition = targetPosition;
        motor.setTargetPosition(targetPosition);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(speed);
        targetPosition = oldTargetPosition + targetPosition;
        if (targetPosition < 0) {
            targetPosition *= -1;
        }
        while (motor.isBusy() && motor.getCurrentPosition() < targetPosition) {
            idle();
        }
        if (!stay) {
            motor.setPower(0);
        }
    }

    protected void driveMotor(DcMotor motor, int targetPosition, double speed) {
        int oldTargetPosition = targetPosition;
        motor.setTargetPosition(targetPosition);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(speed);
        targetPosition = oldTargetPosition + targetPosition;
        if (targetPosition < 0) {
            targetPosition *= -1;
        }
    }
    protected void moveMotor(DcMotor motor, int targetPosition, double speed) {
        moveMotor(motor, targetPosition, speed, false);
    }


    protected void removePower() {
        lf_drive.setPower(0);
        lb_drive.setPower(0);
        rf_drive.setPower(0);
        rb_drive.setPower(0);
    }


    // utility calc methods
    protected int ticsToDegrees(int tics) {
        int degrees = 0;
        double robotLength = 13.62;
        double distUnit = (robotLength) / (Math.cos(45));
        degrees = Math.round((float)(((((tics /intCon)*90)/distUnit)/1.75)));
        return degrees;
    }



    protected void runTasksAsync(List<Runnable> fns) {
        ExecutorService executorService = Executors.newFixedThreadPool(fns.size()); // Thread pool
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        fns.forEach(fn -> futures.add(CompletableFuture.runAsync(fn, executorService)));

        CompletableFuture<Void> allThreads = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allThreads.join();
        executorService.shutdown();
    }
    protected void runTasksAsync(Runnable... fns) {
        runTasksAsync(Arrays.stream(fns).collect(Collectors.toList()));
    }


}
