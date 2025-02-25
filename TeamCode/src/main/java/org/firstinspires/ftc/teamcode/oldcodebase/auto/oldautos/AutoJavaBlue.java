package org.firstinspires.ftc.teamcode.oldcodebase.auto.oldautos;

import org.firstinspires.ftc.teamcode.oldcodebase.auto.AutoJava;

//@Autonomous(name = "AutoJavaBlue", group = "Auto")
public class AutoJavaBlue extends AutoJava {

    public AutoJavaBlue() {
        super(true);
    }

    @Override
    public void runOpMode() {
        this.commonAutoInit();

        //turnBot 97.04347826086958
        moveBot(19.875583203732504,1, 0, 0);
        sleep(500);
        moveMotor(lift, -54, 0.25);
        sleep(500);
        arm.setPosition(0.6094444444444445);
        sleep(500);
        moveBot(0.055987558320373255,-1, 0, 0);
        sleep(500);
        moveMotor(lift, -4491, 0.25);
        sleep(500);
        claw.setPosition(0.3);
        sleep(500);
        arm.setPosition(0.8094444444444444);
        sleep(500);
        claw.setPosition(0.46);
        sleep(500);
        moveMotor(lift, -81, 0.25);
        sleep(500);
        arm.setPosition(0.6088888888888888);
        sleep(500);
        turnBot(98.60869565217392);
        sleep(1000);
        moveBot(4.628304821150855,0,0,1);
        sleep(500);
        moveBot(21.6298600311042,1, 0, 0);
        sleep(500);
        moveBot(1.4183514774494557,1, 0, 0);
        sleep(500);
        claw.setPosition(0.46);
        sleep(500);
        arm.setPosition(0.6083333333333334);
        sleep(500);
        claw.setPosition(0.3);
        sleep(500);
        claw.setPosition(0.46);
        sleep(500);
        arm.setPosition(0.8983333333333334);
        sleep(500);
        arm.setPosition(0.6077777777777778);
        sleep(500);
        turnBot(-138.91304347826087);
        sleep(1000);
        moveBot(5.74805598755832,0,0,-1);
        sleep(500);
        moveBot(18.97978227060653,1, 0, 0);
        sleep(500);
        moveMotor(lift, -4123, 0.25);
        sleep(500);
        claw.setPosition(0.3);
        sleep(500);
        moveMotor(lift, -4160, 0.25);
        sleep(500);
        arm.setPosition(0.7577777777777779);
        sleep(500);
        moveMotor(lift, -82, 0.25);
        sleep(500);
        arm.setPosition(0.6177777777777778);
        sleep(500);

        /*
        moveBotOld(432, 1, 0, 0);
        sleep(3000);
       // sleep(7000);
        moveBotOld(154.11459617621637,1, 0, 0);
        sleep(500);
        arm.setPosition(0.6400000000000001);
        sleep(500);
        turnBot(17);
        sleep(1000);
        moveBotOld(18.5104183758682,1, 0, 0);
        sleep(500);
        moveMotor(lift, -1854, 0.25);
        lift.setPower(0.05);
        sleep(500);
        moveBotOld(20,1, 0, 0);
        sleep(500);
        moveMotor(lift, -800, 0.25);
        lift.setPower(0.05);
        claw.setPosition(0.36);
        sleep(500);

        moveBotOld(160.2083446006954,-1, 0, 0);

        sleep(500);

        moveBotOld(325,0,0,1);
        sleep(500);
        moveBotOld(20,-1, 0, 0);
       // moveMotor(lift, -992, 0.25);
        sleep(500);
       // moveBotOld(80,-1, 0, 0);

       // sleep(500);

       // moveBotOld(340,0,0,1);
        moveMotor(lift, -10, 0.25);*/


        /*
        claw.setPosition(0.9);
        rf_drive.setPower(powerFactor * (-0 + (0 - 1)));
        rb_drive.setPower(powerFactor * (-0 + 0 + 1));
        lf_drive.setPower(powerFactor * (0 + 0 + 1));
        lb_drive.setPower(powerFactor * (0 + (0 - 1)));
        sleep(1200);
        rf_drive.setPower(0);
        rb_drive.setPower(0);
        lf_drive.setPower(0);
        lb_drive.setPower(0);
        /*
        moveBotOld(78.94792324566028,1, 0, 0);
        sleep(1000);
//        moveBotOld(324.385443698787,1, 0, 0);
        // move forward
        // lift up
        liftBot(2);
//        moveMotor(lift, -1300, 0.05);
        sleep(1000);
        moveBotOld(6.916667659722306,1, 0, 0);
        sleep(1000);
        // move forward slightly
        // lift down slightly
        liftBot(1);
        sleep(1000);
        claw.setPosition(0.055);
        sleep(1000);
        moveBotOld(11.916667659722306,-1, 0, 0);

        // let go claw
        // lift back up
        // move backward slightly
        // lift down
        // ....*/
    }
}
