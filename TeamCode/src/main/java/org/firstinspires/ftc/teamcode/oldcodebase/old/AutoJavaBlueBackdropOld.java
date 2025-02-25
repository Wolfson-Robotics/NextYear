package org.firstinspires.ftc.teamcode.oldcodebase.old;


//@Autonomous(name = "AutoJavaBlueBackdropOld", group = "Auto")
public class AutoJavaBlueBackdropOld extends AutoJavaOld {


    public AutoJavaBlueBackdropOld() {
        super(true);
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
