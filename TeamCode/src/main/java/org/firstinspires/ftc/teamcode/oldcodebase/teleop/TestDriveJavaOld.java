package org.firstinspires.ftc.teamcode.oldcodebase.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.oldcodebase.RobotBase;

@TeleOp(name = "TestDriveJavaOld", group = "Auto")
public class TestDriveJavaOld extends RobotBase {

    private double powerFactor = 1.25;

    @Override
    public void runOpMode() {
        this.initMotors();
        telemetry.addLine("Waiting for start");
        telemetry.update();

        waitForStart();
        double currentArmPosition = 0.35;
        double experimentalArmPos = currentArmPosition;
        while (opModeIsActive()) {
            moveServo(arm, currentArmPosition, 20);
            lift.setPower(gamepad2.right_stick_y * 0.5);
            if (gamepad2.left_stick_y > 0) {
                currentArmPosition += 0.01; // increase by a small step
                if(currentArmPosition > 1) currentArmPosition = 1;
            } else if (gamepad2.left_stick_y < 0) {
                currentArmPosition -= 0.01; // decrease by a small steps
                if(currentArmPosition < -1) currentArmPosition = -1;
            }
            if (gamepad1.y) {
                telemetry.addLine("Lift move up");
                telemetry.update();
                lift.setPower(-.4);
                sleep(100);
                lift.setPower(0);
            }
            if (gamepad1.a) {
                telemetry.addLine("Lift move down");
                telemetry.update();
                lift.setPower(.4);
                sleep(100);
                lift.setPower(0);
            }
            telemetry.addData("arm: ", currentArmPosition);
            telemetry.addData("arm pos: ", arm.getPosition());
            telemetry.addData("lift tic: ", lift.getCurrentPosition());
            telemetry.addData("lift power sent: ", gamepad2.right_stick_y);
            telemetry.update();
            if (gamepad2.left_trigger > 0) {
                claw.setPosition(0.18);
            } //grab claw
            if (gamepad2.right_trigger > 0) {
                claw.setPosition(0.06);
            }//drop


            moveBot(-gamepad1.left_stick_y, (gamepad1.right_stick_x), gamepad1.left_stick_x);
//            moveBot(-gamepad1.left_stick_y, (gamepad1.right_stick_x), gamepad1.left_stick_x);
        }
    }


    private void moveBot(float vertical, float pivot, float horizontal) {
        pivot *= 0.6;
        rf_drive.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb_drive.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf_drive.setPower(powerFactor * (pivot + vertical + horizontal));
        lb_drive.setPower(powerFactor * (pivot + (vertical - horizontal)));
    }

}
