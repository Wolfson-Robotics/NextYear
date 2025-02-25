package org.firstinspires.ftc.teamcode.oldcodebase.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoJavaPark", group = "Auto")
public class AutoJavaPark extends AutoJava {

    public AutoJavaPark() {
        super(true);
    }

    @Override
    public void runOpMode() {
        this.commonAutoInit();
        waitForStart();

        moveBot(35, 1, 0, 0);
        moveBot(5, 0, 0, 1);

    }


}
