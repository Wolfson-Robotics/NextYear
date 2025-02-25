package org.firstinspires.ftc.teamcode.oldcodebase.auto.oldautos;

import org.firstinspires.ftc.teamcode.oldcodebase.auto.AutoJava;

//@Autonomous(name = "AutoJavaRed", group = "Auto")
public class AutoJavaRed extends AutoJava {

    public AutoJavaRed() {
        super(false);
    }

    @Override
    public void runOpMode() {
        this.commonAutoInit();
        moveBot(5, 1, 0, 0);
        sleep(500);
        moveBotDiag(20, 40, 1, 1);

        /*
        moveBot(47.78125398177117,0,0,1);
        arm.setPosition(0.6400000000000001);
        moveBot(50,1, 0, 0);
        sleep(150);
        turnBot(-33);
        sleep(200);
        moveBot(20,1, 0, 0);
        sleep(150);
        turnBot(10);
        sleep(250);
        moveMotor(lift, -4217, 0.25, true);
        powerFactor= 0.4;
        sleep(100);
        moveBot(43,1, 0, 0);
        sleep(100);
        arm.setPosition(0.75);
        sleep(100);
        turnBot(-33);
        claw.setPosition(0.36);
        sleep(250);
        arm.setPosition(0.5);
        moveBot(46.40625386718783,-1, 0, 0);
        sleep(500);
        moveMotor(lift, -10, 0.25, true);
        powerFactor = 1;*/


        /*
        moveBot(35, 1, 0, 0);
        telemetry.addData("lf_drive pos: ", lf_drive.getCurrentPosition());
        telemetry.addData("rf_drive pos: ", rf_drive.getCurrentPosition());
        telemetry.update();
        sleep(2000);
        moveBot(35, -1, 0, 0);
        telemetry.addData("lf_drive pos: ", lf_drive.getCurrentPosition());
        telemetry.addData("rf_drive pos: ", rf_drive.getCurrentPosition());
        telemetry.update();
        sleep(2000);
        moveBot(35, 0, 0, 1);
        telemetry.addData("lf_drive pos: ", lf_drive.getCurrentPosition());
        telemetry.addData("rf_drive pos: ", rf_drive.getCurrentPosition());
        telemetry.update();
        sleep(2000);
        moveBot(35, 0, 0, -1);
        telemetry.addData("lf_drive pos: ", lf_drive.getCurrentPosition());
        telemetry.addData("rf_drive pos: ", rf_drive.getCurrentPosition());
        telemetry.update();
        sleep(10000);*/
//        moveMotor(lift, -1300, 0.05);
        //liftBot(2);
        /*
        sleep(1000);
//        moveMotor(lift, -800);
//        while (opModeIsActive()) {
            lf_drive.setPower(0.155);
            lb_drive.setPower(0.155);
            rf_drive.setPower(0.155);
            rb_drive.setPower(0.0155);
            sleep(550);
            removePower();
            telemetry.addLine(String.valueOf(lf_drive.getCurrentPosition()));
            telemetry.update();
            sleep(10000);*/
//        }
        // move forward
        // lift up
        // move forward slightly
        // lift down slightly
        // let go claw
        // lift back up
        // move backward slightly
        // lift down
        // ....
    }
}
