package org.firstinspires.ftc.teamcode.oldcodebase.old;

//@Autonomous(name = "AutoJavaRedOld", group = "Auto")
public class AutoJavaRedOld extends AutoJavaOld {


    public AutoJavaRedOld() {
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
            switch (pixelDetection.getPosition()) {

                case LEFT: {


                    break;

                }

                case CENTER: {


                    break;

                }

                case RIGHT: {



                    break;

                }

            }


        }



    }
}
