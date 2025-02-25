package org.firstinspires.ftc.teamcode.oldcodebase.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.concurrent.atomic.AtomicBoolean;

@Autonomous(name = "AutoJavaFourYellowSample", group = "Auto")
public class AutoJavaFourYellowSample extends AutoJava {

    public AutoJavaFourYellowSample() {
        super(true);
    }

    @Override
    public void runOpMode() {
        this.commonAutoInit();
/*
        // preloaded sample init
        runTasksAsync(
                () -> topBasketLift(),
                () -> {
//                    moveBot(3.55, 0, 0, 0.65);
                    moveBot(2.45, 0, 0, 0.65);
                    sleep(150);
                    moveBot(15.819595645412130745, 0.65, 0, 0);
                }
                );
        sleep(150);
        // preloaded sample basket
        moveBot(4.15, 0.5, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(150);
        restArm();
        sleep(350);
        moveBot(5.15, -0.5, 0, 0);
        sleep(100);
        // right sample init basket -> ready to start right sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {
                    turnBot(85);
                    sleep(250);
//                    moveBot(5, 0, 0, 0.75);
                    moveBot(8, 0, 0, 0.75);
                    sleep(50);
                    moveBot(18.367, 0.75, 0, 0);
                }
                );
        // right sample grab sample
        sleep(250);
        goToSample();
        sleep(100);
        grabSample();
        sleep(150);
        // right sample turn back to go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {
                    turnBot(-126.5);
                    sleep(200);
                    moveBot(15, 0.65, 0, 0);
                }
                );
        sleep(150);
        // right sample basket operation
        moveBot(4, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(350);
        restArm();
        sleep(350);
        moveBot(6, -0.6, 0, 0);
        sleep(250);
        // middle sample init go to sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {
                    turnBot(146);
                    sleep(150);
//                    moveBot(5, 0, 0, 0.75);
                    moveBot(8, 0, 0, 0.75);
                    sleep(300);
                    moveBot(7.125, 0.75, 0, 0);
                }
        );
        // middle sample grab sample
        sleep(150);
        goToSample();
        sleep(100);
        grabSample();
        sleep(150);
        moveBot(4, 0, 0, 1);
        // middle sample go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {
                    turnBot(165.11739130434784);
                    sleep(150);
                    moveBot(8, 0.65, 0, 0);
                    turnBot(65.926086956521742);
                }
        );
        // middle sample basket operation
        sleep(150);
        moveBot(9.85, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(150);
        restArm();
        sleep(350);
        moveBot(10.155, -0.6, 0, 0);
        sleep(150);
        // left sample init go to sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.46),
                () -> {
                    turnBot(115);
                    sleep(150);
                    sleep(250);
                    moveBot(14.277, 0, 0, -0.75);
                    sleep(150);
                    moveBot(11.985, 0.75, 0, 0);
                    sleep(150);
                    turnBot(-16.434782608695652);
                    sleep(250);
                    moveBot(2.202177293934681, 0.75, 0, 0);
                    sleep(150);
                    turnBot(2.391304347826088);
                }
                );
        // left sample grab sample
        sleep(150);
        moveBot(1.75, -0.5, 0, 0);
        // grabSample
        moveServo(arm, 0.8);
        sleep(150);
        moveServo(claw, 0.15);
        sleep(150);
        moveServo(arm, 0.91);
        sleep(250);
        moveServo(claw, 0.46);
        sleep(300);
        runTasksAsync(
                () -> topBasketLift(),
                () -> restArm(),
                () -> {
                    moveBot(20.771384136858476, -0.75, 0, 0);
                    sleep(150);
                    turnBot(-85);
                }
                );
        sleep(150);
        moveBot(5.74805598755832, 0.745, 0, 0);
        sleep(150);
        sampleInBasket();
        arm.setPosition(0.6);
        sleep(250);
        restArm();
       // sleep(250);
        //moveBot(2.465007776049767, -0.745, 0, 0);
       // sleep(150);
       // restLift();*/

/*
        // preloaded sample init
        runTasksAsync(
                () -> topBasketLift(),
                () -> {
                    moveBot(2.45, 0, 0, 0.65);
                    sleep(150);
                    moveBot(15.819595645412130745, 0.65, 0, 0);
                }
                );
        sleep(150);

// preloaded sample basket
        moveBot(4.15, 0.5, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(150);
        restArm();
        sleep(350);
        moveBot(5.15, -0.5, 0, 0);
        sleep(100);

// right sample init basket -> ready to start right sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {
                    turnBot(90);
                    sleep(250);
                    moveBot(8, 0, 0, 0.75);
                    sleep(50);
                    moveBot(18.367, 0.75, 0, 0);
                }
                );

// right sample grab sample
        sleep(250);
        goToSample();
        moveBot(2, 0, 0, -1);

//sleep 100
        grabSample();
        sleep(150);

// right sample turn back to go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {

//turnBot -126.5
                    turnBot(-120.5);
                    sleep(200);
                    moveBot(15, 0.65, 0, 0);
                }
                );
        sleep(150);

// right sample basket operation
        moveBot(4, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(350);
        restArm();
        sleep(350);
        moveBot(6, -0.6, 0, 0);
        sleep(250);

// middle sample init go to sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {
                    turnBot(146);
                    sleep(150);
                    moveBot(8, 0, 0, 0.75);
                    sleep(300);
                    moveBot(7.125, 0.75, 0, 0);
                }
                );

// middle sample grab sample
        sleep(150);
        goToSample();
        moveBot(2, 0, 0, -1);
        moveBot(1.75, 1, 0, 0);
        grabSample();

//sleep 150
        moveBot(4, 0, 0, 1);

// middle sample go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {
                    turnBot(165.11739130434784);
                    sleep(150);
                    moveBot(8, 0.65, 0, 0);
                    turnBot(65.926086956521742);
                }
                );

// middle sample basket operation
        sleep(150);
        moveBot(9.85, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(150);
        restArm();
        sleep(350);
        moveBot(10.155, -0.6, 0, 0);
        moveMotor(lift, 0, 1);
        sleep(150);*/
/*
        // preloaded sample init
        runTasksAsync(
                () -> topBasketLift(),
                () -> {
                    moveBot(2.45, 0, 0, 0.65);
                    sleep(150);
                    moveBot(15.819595645412130745, 0.65, 0, 0);
                }
                );
        sleep(150);

// preloaded sample basket
        moveBot(4.15, 0.5, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(150);
        restArm();
        sleep(350);
        moveBot(5.15, -0.5, 0, 0);
        sleep(100);

// right sample init basket -> ready to start right sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {
                    turnBot(90);
                    sleep(250);
                    moveBot(7, 0, 0, 0.75);
                    sleep(50);
                    moveBot(18.367, 0.75, 0, 0);
                }
                );

// right sample grab sample
        sleep(250);
        goToSample();
        moveBot(3.5, 0, 0, -1);
        sleep(50);
        moveBot(2, 1, 0, 0);
        sleep(50);

//sleep 100
        grabSample();
        sleep(150);

// right sample turn back to go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {

//turnBot -126.5
                    turnBot(-120.5);
                    sleep(200);
                    moveBot(15, 0.65, 0, 0);
                }
                );
        sleep(150);

// right sample basket operation
        moveBot(4, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(350);
        restArm();
        sleep(350);
        moveBot(6, -0.6, 0, 0);
        sleep(250);*/
/*
        // preloaded sample init
        runTasksAsync(
                () -> topBasketLift(),
                () -> {
                    moveBot(2.45, 0, 0, 0.65);
                    sleep(50);
                    moveBot(15.819595645412130745, 0.65, 0, 0);
                }
                );
        sleep(150);

// preloaded sample basket
        moveBot(4.15, 0.5, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(150);
        restArm();
        sleep(350);
        moveBot(5.15, -0.5, 0, 0);
        sleep(100);

// right sample init basket -> ready to start right sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {
                    turnBot(90);

//sleep 50

//moveBot 7 0 0 0.75
                    moveBot(5, 0, 0, 0.75);

//sleep 50
                    moveBot(18.267, 0.75, 0, 0);

//moveBot 29.367 0.75 0 0
                }
                );

// right sample grab sample
        sleep(250);
        goToSample();

//moveBot 3.5 0 0 -1

//sleep 50
        moveBot(2.25, 1, 0, 0);
        sleep(50);

//sleep 100
        grabSample();
        sleep(150);

//moveBot 1 -1 0 0

// right sample turn back to go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {
                    turnBot(-133.5);

//turnBot -120.5
                    sleep(200);
                    moveBot(15, 0.65, 0, 0);
                }
                );
        sleep(150);

// right sample basket operation
        moveBot(4, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(350);
        restArm();
        sleep(350);
        moveBot(6, -0.6, 0, 0);
        sleep(250);

// middle sample init go to sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {

// turnBot 146
                    turnBot(130);
                    sleep(150);
                    moveBot(4.1, 0, 0, 0.75);
                    sleep(300);
                    moveBot(7.125, 0.75, 0, 0);
                }
                );

// middle sample grab sample
        sleep(150);
//        goToSample();
        moveBot(5.25, 0, 0, -1);
        sleep(50);
        moveBot(1.75, 1, 0, 0);
        grabSample();

//sleep 150
        moveBot(4, 0, 0, 1);

// middle sample go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {
                    turnBot(165.11739130434784);
                    sleep(150);
                    moveBot(8, 0.65, 0, 0);
                    turnBot(65.926086956521742);
                }
                );

// middle sample basket operation
        sleep(150);
        moveBot(9.85, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(450);
        restArm();
        sleep(550);
        moveBot(13.155, -0.6, 0, 0);
        sleep(550);
        moveMotor(lift, 0, 1);
        sleep(150);*/

        // preloaded sample init
        runTasksAsync(
                () -> topBasketLift(),
                () -> {
                    moveBot(2.45, 0, 0, 0.65);
                    sleep(50);
                    moveBot(15.819595645412130745, 0.65, 0, 0);
                }
                );
        sleep(150);

// preloaded sample basket
        moveBot(4.15, 0.5, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(150);
        restArm();
        sleep(350);
        moveBot(5.15, -0.5, 0, 0);
        sleep(100);

// right sample init basket -> ready to start right sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {
                    turnBot(85);

//turnBot 101

//sleep 50

//moveBot 7 0 0 0.75
                    moveBot(5, 0, 0, 0.75);

//sleep 50
                    moveBot(18.267, 0.75, 0, 0);

//moveBot 29.367 0.75 0 0
                }
                );

// right sample grab sample
        sleep(250);
        goToSample();

//moveBot 3.5 0 0 -1

//sleep 50
        moveBot(2.25, 1, 0, 0);
        sleep(50);

//sleep 100
        grabSample();
        sleep(150);

//moveBot 1 -1 0 0

// right sample turn back to go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {

// turnBot -133.5

// turnBot -143.5
                    turnBot(-125);

//turnBot -120.5
                    sleep(200);
                    moveBot(15, 0.65, 0, 0);
                }
                );
        sleep(150);

// right sample basket operation
        moveBot(4, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(350);
        restArm();
        sleep(350);
        moveBot(6, -0.6, 0, 0);
        sleep(250);

// middle sample init go to sample
        runTasksAsync(
                () -> restLift(),
                () -> moveServo(claw, 0.15),
                () -> {

// turnBot 146
                    turnBot(130);
                    sleep(150);

//moveBot 4.1 0 0 0.75
                    moveBot(2.5, 0, 0, 0.75);
                    sleep(300);
                    moveBot(7.125, 0.75, 0, 0);
                }
                );

// middle sample grab sample

//sleep 150
        //goToSample();

//moveBot 1.75 1 0 0
        grabSample();

//sleep 150
        moveBot(4, 0, 0, 1);

// middle sample go to basket
        runTasksAsync(
                () -> restArm(),
                () -> topBasketLift(),
                () -> {
                    turnBot(165.11739130434784);
                    sleep(100);
                    moveBot(8, 0.65, 0, 0);
                    turnBot(75.926086956521742);
                }
                );

// middle sample basket operation
        sleep(100);

//moveBot 9.85 0.65 0 0
        moveBot(12, 0.65, 0, 0);
        sleep(150);
        sampleInBasket();
        sleep(150);
        restArm();
        sleep(350);
        moveBot(10.155, -0.6, 0, 0);
        moveMotor(lift, 0, 1);
        sleep(150);
    }
}
