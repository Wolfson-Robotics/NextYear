package org.firstinspires.ftc.teamcode.oldcodebase.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.oldcodebase.RobotBase;

@TeleOp(name = "TestDriveJava")
public class TestDriveJava extends RobotBase {

    @Override
    public void runOpMode() {
        this.initMotors();
        waitForStart();

        while (opModeIsActive()) {
            slide1.setPower(gamepad1.dpad_up ? 1 : (gamepad1.dpad_down) ? -1 : 0);
            slide2.setPower(gamepad1.dpad_up ? 1 : (gamepad1.dpad_down) ? -1 : 0);
            telemetry.addLine("slide power: " + slide1.getPower());
            telemetry.update();
        }
    }
}
