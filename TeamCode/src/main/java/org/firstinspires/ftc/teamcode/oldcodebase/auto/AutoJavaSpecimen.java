package org.firstinspires.ftc.teamcode.oldcodebase.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoJavaSpecimen", group = "Auto")
public class AutoJavaSpecimen extends AutoJava {

    public AutoJavaSpecimen() {
        super(true);
    }

    @Override
    public void runOpMode() {
        moveServo(claw, 0.46);
        sleep(150);
        moveBot(25.44167962674961, 1, 0, 0);
        sleep(150);
        moveMotor(lift, -2160, 0.75);
        sleep(150);
        moveBot(3.5, 1, 0, 0);
        sleep(150);
        moveMotor(lift, -989, 0.5);
        sleep(150);
        moveServo(claw, 0.3);
        sleep(150);
        sleep(150);
//        moveBot(4, -1, 0, 0);
        moveBot(10, -1, 0, 0);
        sleep(150);
//        moveBot(20, 0, 0, 1);
    }
}
