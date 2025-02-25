package  org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.handler.DcMotorExHandler;

public abstract class RobotBase extends LinearOpMode {

    protected DcMotorExHandler lf_drive;
    protected DcMotorExHandler lb_drive;
    protected DcMotorExHandler rf_drive;
    protected DcMotorExHandler rb_drive;


    public RobotBase() {
        this.rf_drive = new DcMotorExHandler(hardwareMap.get(DcMotorEx.class, "rf_drive"));
        this.rb_drive = new DcMotorExHandler(hardwareMap.get(DcMotorEx.class, "rb_drive"));
        this.lf_drive = new DcMotorExHandler(hardwareMap.get(DcMotorEx.class, "lf_drive"));
        this.lb_drive = new DcMotorExHandler(hardwareMap.get(DcMotorEx.class, "lb_drive"));

        this.lf_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        this.lb_drive.setDirection(DcMotorSimple.Direction.REVERSE);
    }


}