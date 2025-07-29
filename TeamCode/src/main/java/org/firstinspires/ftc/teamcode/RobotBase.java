package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.handlers.HandlerMap;
import org.firstinspires.ftc.teamcode.handlers.HardwareComponentHandler;
import org.firstinspires.ftc.teamcode.handlers.ServoHandler;
import org.firstinspires.ftc.teamcode.handlers.camera.CameraHandler;
import org.firstinspires.ftc.teamcode.handlers.camera.OpenCvCameraHandler;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.lang.reflect.Field;

public abstract class RobotBase extends OpMode {

    protected DcMotorExHandler lf_drive, lb_drive, rf_drive, rb_drive;

    protected DcMotorExHandler lift;

    // Temporarily carry over components from the Into the Deep season
    // for testing for now
    protected ServoHandler arm;
    protected ServoHandler claw;

    // Second intake servos
    protected DcMotorExHandler slide1;
    protected DcMotorExHandler slide2;
    protected ServoHandler slideArm;
    protected CRServo leftRoller;
    protected CRServo rightRoller;

    protected CameraHandler<? extends CameraStreamSource> camera;

    protected final String storagePath = Environment.getExternalStorageDirectory().getPath();
    protected final String logsPath = storagePath + "/Logs/";



    public void init() {

        DcMotorExHandler.setHardwareMap(hardwareMap);
        ServoHandler.setHardwareMap(hardwareMap);
        this.rf_drive = new DcMotorExHandler("rf_drive", false);
        this.rb_drive = new DcMotorExHandler("rb_drive", false);
        this.lf_drive = new DcMotorExHandler("lf_drive", false);
        this.lb_drive = new DcMotorExHandler("lb_drive", false);

//        this.lift = new DcMotorExHandler(hardwareMap.get(DcMotorEx.class, "lift"));
        this.arm = new ServoHandler("arm");
        this.claw = new ServoHandler("claw");


//        this.slide1 = new DcMotorExHandler(hardwareMap.get(DcMotorEx.class, "slide1"));
//        this.slide2 = new DcMotorExHandler(hardwareMap.get(DcMotorEx.class, "slide2"));

//        this.slideArm = new ServoHandler(hardwareMap.get(Servo.class, "slidearm"));
//        this.slideArm.setDirection(Servo.Direction.REVERSE);

//        this.leftRoller = hardwareMap.get(CRServo.class, "leftroller");
//        this.rightRoller = hardwareMap.get(CRServo.class, "rightroller");

        this.lf_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        this.lb_drive.setDirection(DcMotorSimple.Direction.REVERSE);
//        this.lift.setDirection(DcMotorSimple.Direction.REVERSE);

        this.arm.scaleRange(0.75, 1);
        this.arm.min();

        this.claw.scaleRange(0.36, 0.46);
        this.claw.max();

        this.registerHandlers();
    }

    public void registerHandlers() {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!HardwareComponentHandler.class.isAssignableFrom(field.getType())) {
                continue;
            }
            HardwareComponentHandler<?> handler;
            try {
                handler = (HardwareComponentHandler<?>) field.get(this);
            } catch (IllegalAccessException e) {
                continue;
            }
            HandlerMap.put(handler.getName(), handler);
        }
    }

    protected boolean isControlled(double control) {
        return Math.abs(control) > 0.1;
    }


}