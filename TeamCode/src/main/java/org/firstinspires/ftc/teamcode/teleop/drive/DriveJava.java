package org.firstinspires.ftc.teamcode.teleop.drive;

import org.firstinspires.ftc.teamcode.RobotBase;

public class DriveJava extends RobotBase {

    @Override
    public void runOpMode() {

        ControllerHandler[] handlers = new ControllerHandler[] {
                new ControllerHandler(
                        arm,
                        () -> arm.setPower(gamepad2.left_stick_y),
                        Preset.of(() -> gamepad2.dpad_up, arm.getMin()),
                        Preset.of(() -> gamepad2.dpad_down, arm.getMax()),
                        Preset.of(() -> gamepad2.dpad_right, (arm.getMax() + arm.getMin()) / (double) 2)
                ),
                new ControllerHandler(
                        slideArm,
                        Preset.of(() -> gamepad2.a, slideArm.getMin()),
                        Preset.of(() -> gamepad2.y, slideArm.getMax()),
                        Preset.of(() -> gamepad2.x, 0.5)
                )
        };

        waitForStart();
        while (opModeIsActive()) {

            for (ControllerHandler handle : handlers) handle.handle();

            lift.control(gamepad2.right_stick_y);
            slide1.control(gamepad2.dpad_left, gamepad2.dpad_right);
            slide2.control(gamepad2.dpad_left, gamepad2.dpad_right);
            claw.toggle(gamepad2.left_trigger > 0.1, gamepad2.right_trigger > 0.1);



            if (gamepad2.right_bumper) {
                leftRoller.setPower(-1);
                rightRoller.setPower(1);
            } else {
                if (gamepad2.left_bumper) {
                    leftRoller.setPower(1);
                    rightRoller.setPower(-1);
                } else {
                    leftRoller.setPower(0);
                    rightRoller.setPower(0);
                }
            }
            // TODO: Implement moveBot
//            moveBot(-gamepad1.left_stick_y, (gamepad1.right_stick_x), gamepad1.left_stick_x);

            telemetry.addData("lift power sent: ", gamepad2.right_stick_y);
            telemetry.addData("lift pos actual: ", lift.getPosition());
            telemetry.addData("lift power actual: ", lift.getPower());
            telemetry.addData("slide pos actual: ", slide1.getPosition());
            telemetry.addData("slide power: ", slide1.getPower());
            telemetry.addData("arm pos actual: ", arm.getPosition());
            telemetry.addData("claw pos: ", claw.getPosition());
            telemetry.addData("lf_drive pos: ", lf_drive.getPosition());
            telemetry.addData("lf_drive power: ", lf_drive.getPower());
            telemetry.addData("rf_drive pos: ", rf_drive.getPosition());
            telemetry.addData("rf_drive power: ", rf_drive.getPower());
            telemetry.addData("gamepad2 right trigger: ", gamepad2.right_trigger);
            telemetry.addData("gamepad2 left trigger: ", gamepad2.left_trigger);
            telemetry.addData("gamepad2 right stick y: ", gamepad2.right_stick_y);
            telemetry.addData("gamepad1 left stick x: ", gamepad1.left_stick_x);
            telemetry.addData("gamepad1 left stick y: ", gamepad1.left_stick_y);
            telemetry.addData("gamepad1 right stick x: ", gamepad1.right_stick_x);
            telemetry.addData("gamepad1 right stick y: ", gamepad1.right_stick_y);
            telemetry.addData("power factor:", 0);
            telemetry.addLine(" ");
            telemetry.addLine(" ");
            telemetry.addData("gamepad2 right bumper: ", gamepad2.right_bumper);
            telemetry.addData("gamepad2 left bumper: ", gamepad2.left_bumper);
            telemetry.addData("gamepad2 dpad up: ", gamepad2.dpad_up);
            telemetry.addData("gamepad2 dpad down: ", gamepad2.dpad_down);
            telemetry.addData("gamepad1 dpad up: ", gamepad1.dpad_down);
            telemetry.addData("gamepad1 dpad down: ", gamepad1.dpad_down);
            telemetry.addData("gamepad2 a: ", gamepad2.a);
            telemetry.addData("gamepad2 b: ", gamepad2.b);
            telemetry.addData("gamepad2 y: ", gamepad2.y);
            telemetry.update();


        }
    }

}
