package org.firstinspires.ftc.teamcode.teleop.drive;

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
        DcMotorEx lf_drive_raw = hardwareMap.get(DcMotorEx.class, "lf_drive");
        DcMotorExHandler lf_drive = new DcMotorExHandler(lf_drive_raw);
        lf_drive.setPositionBounds(-130, 50);
        waitForStart();
        while (opModeIsActive()) {
            lf_drive.setPower(gamepad1.left_stick_y);
            telemetry.addData("gamepad1 left stick y", gamepad1.left_stick_y);
            telemetry.addData("power", lf_drive.getPower());
            telemetry.addData("pos", lf_drive.getPosition());
            telemetry.update();
        }
    }
}
