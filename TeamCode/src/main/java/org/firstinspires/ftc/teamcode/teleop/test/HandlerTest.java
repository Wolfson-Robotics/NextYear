package org.firstinspires.ftc.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.handlers.DcMotorExHandler;

@TeleOp(name = "HandlerTest")
public class HandlerTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        telemetry.addData("voltage", hardwareMap.voltageSensor.get("Control Hub").getVoltage());
        telemetry.update();
        DcMotorEx rb_drive_raw = hardwareMap.get(DcMotorEx.class, "rb_drive");
        DcMotorExHandler rb_drive = new DcMotorExHandler(rb_drive_raw);
//        rb_drive.setPositionBounds(-130, 50);
        waitForStart();

        boolean running = false;
        while (opModeIsActive()) {
            rb_drive.setPower(gamepad1.left_stick_y);
            telemetry.addData("gamepad1 left stick y", gamepad1.left_stick_y);
            telemetry.addData("power", rb_drive.getPower());
            telemetry.addData("pos", rb_drive.getPosition());
            telemetry.update();
        }
    }
}
