package org.firstinspires.ftc.teamcode.oldcodebase.old;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//@Autonomous(name = "AutoJavaRedBackdrop", group = "Auto")
public class AutoJavaRedBackdrop extends AutoJavaOld {


    public AutoJavaRedBackdrop() {
        super(false);
    }


    @Override
    public void runOpMode() {

        initMotors();
        initCamera();


        while (!isStarted()) {
            telemetry.addData("White percent of LCR mats:", pixelDetection.getLeftPercent() + " "
                    + pixelDetection.getCenterPercent() + " " + pixelDetection.getRightPercent());
            telemetry.addData("ROTATION1: ", pixelDetection.getPosition());
            telemetry.update();
        }


        telemetry.addLine("Waiting for start");
        telemetry.update();
        waitForStart();

        camera.closeCameraDevice();
        while (opModeIsActive()) {

            // robot must be 8.5 inches away from the left of the robot
            switch (pixelDetection.getPosition()) { //

                // Completely working
                case LEFT: {


                    break;

                }

                case CENTER: {


                    break;

                }

                // Completely working
                case RIGHT: {


                    break;

                }

            }
            break;


        }



    }

}
