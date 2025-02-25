package org.firstinspires.ftc.teamcode.oldcodebase.old;


//@Autonomous(name = "AutoJavaBlueOld", group = "Auto")
public class AutoJavaBlueOld extends AutoJavaOld {


    public AutoJavaBlueOld() {
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
