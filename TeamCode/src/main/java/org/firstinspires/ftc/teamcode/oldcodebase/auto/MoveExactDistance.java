package org.firstinspires.ftc.teamcode.oldcodebase.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "MoveExactDistance(20 inches)", group = "Auto")
public class MoveExactDistance extends AutoJava {

    //Todo: update the vars here
    static final double     COUNTS_PER_MOTOR_REV    = 28;
    static final double     DRIVE_GEAR_REDUCTION    = 1;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 3.5;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    public MoveExactDistance() {
        super(true);
    }

    @Override
    public void runOpMode() {

        this.initMotors();

        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();
        lf_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lb_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rb_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lf_drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lb_drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf_drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rb_drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //indicate that the encoders were reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                lf_drive.getCurrentPosition(),
                lb_drive.getCurrentPosition(),
                rf_drive.getCurrentPosition(),
                rb_drive.getCurrentPosition()
        );
        telemetry.update();

        waitForStart();
        encoderDrive(DRIVE_SPEED,  20, 5.0);
    }

    ElapsedTime runtime = new ElapsedTime();
    //Todo: refactor proper
    public void encoderDrive(double speed, double distanceIn, double timeout) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = lf_drive.getCurrentPosition() + (int) (distanceIn * COUNTS_PER_INCH);
            newRightTarget = rf_drive.getCurrentPosition() + (int) (distanceIn * COUNTS_PER_INCH);
            lf_drive.setTargetPosition(newLeftTarget);
            lb_drive.setTargetPosition(newLeftTarget);
            rf_drive.setTargetPosition(newRightTarget);
            rb_drive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            lf_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lb_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rf_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rb_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // reset the timeout time and start motion.
            runtime.reset();
            lf_drive.setPower(Math.abs(speed));
            lb_drive.setPower(Math.abs(speed));
            rf_drive.setPower(Math.abs(speed));
            rb_drive.setPower(Math.abs(speed));


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() && (runtime.seconds() < timeout) &&
                    lf_drive.isBusy()  && rf_drive.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d  %7d :%7d",
                        lf_drive.getCurrentPosition(),
                        rf_drive.getCurrentPosition(),
                        lb_drive.getCurrentPosition(),
                        rb_drive.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            lf_drive.setPower(0);
            lb_drive.setPower(0);
            rf_drive.setPower(0);
            rb_drive.setPower(0);

            // Turn off RUN_TO_POSITION
            lf_drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lb_drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rf_drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rb_drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //  sleep(250);   // optional pause after each move
        }
    }


}