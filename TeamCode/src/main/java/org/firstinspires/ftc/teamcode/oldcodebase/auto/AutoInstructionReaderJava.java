package org.firstinspires.ftc.teamcode.oldcodebase.auto;

import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.endLinearRunnableMarker;
import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.endThreadingMarker;
import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.linearRunnableMarker;
import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.multiCommentBegin;
import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.multiCommentEnd;
import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.multiThreadingMarker;
import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.singleCommentMarker;
import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.stopMarker;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants;
import org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionReader;
import org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Autonomous(name = "AutoInstructionReaderJava", group = "Auto")
public class AutoInstructionReaderJava extends AutoJava {

    private final ArrayList<?> dummyArr = new ArrayList<>();

    public AutoInstructionReaderJava() { super(true); }

    @Override
    public void runOpMode() {

        this.commonAutoInit();
        telemetry.addLine("Waiting for start");
        telemetry.addData("lowest junk", lowestJunk);
        telemetry.addData("highest junk", highestJunk);
        telemetry.update();
        waitForStart();


        telemetry.addLine("Parsing instructions...");
        telemetry.update();
        try {

            AutoInstructionReader reader = new AutoInstructionReader(AutoInstructionConstants.autoInstructPath);

            AutoOperation autoOperation;
            boolean skipOperations = false, storeAsync = false, creatingLinearRunnable = false;
            List<Runnable> fns = new ArrayList<>(), fnsInFn = new ArrayList<>();
            while ((autoOperation = reader.readLine()) != null) {


                final String operationName = autoOperation.getOperationName();
                final ArrayList<String> operationArgs = autoOperation.getOperationArgs();

                boolean skipCurrOperation = false;
                switch(operationName) {
                    case singleCommentMarker:
                        skipCurrOperation = true;
                        break;
                    case multiCommentBegin:
                        skipOperations = true;
                        break;
                    case multiCommentEnd:
                        skipOperations = false;
                        skipCurrOperation = true;
                        break;
                    case stopMarker:
                        if (skipCurrOperation || skipOperations) {
                            break;
                        }
                        return;
                    case multiThreadingMarker:
                        storeAsync = true;
                        break;
                    case endThreadingMarker:
                        storeAsync = false;
                        break;
                    case linearRunnableMarker:
                        creatingLinearRunnable = true;
                        break;
                    case endLinearRunnableMarker:
                        creatingLinearRunnable = false;
                        break;
                }
                if (skipCurrOperation || skipOperations) {
                    continue;
                }



                Runnable fn = null;
                switch (operationName) {

                    case "moveBot":
                        ArrayList<Double> moveBotArgs = operationArgs.stream()
                                .map(String::valueOf)
                                .map(Double::parseDouble)
                                .collect(Collectors.toCollection(ArrayList::new));
                        fn = () -> moveBot(moveBotArgs.get(0), moveBotArgs.get(1), moveBotArgs.get(2), moveBotArgs.get(3));
                        break;
                    case "moveBotOld":
                        ArrayList<Double> moveBotOldArgs = operationArgs.stream()
                                .map(String::valueOf)
                                .map(Double::parseDouble)
                                .collect(Collectors.toCollection(ArrayList::new));
                        fn = () -> moveBotOld(moveBotOldArgs.get(0), moveBotOldArgs.get(1), moveBotOldArgs.get(2), moveBotOldArgs.get(3));
                        break;
                    case "moveBotDiag":
                        ArrayList<Double> moveBotDiagArgs = operationArgs.stream()
                                .map(String::valueOf)
                                .map(Double::parseDouble)
                                .collect(Collectors.toCollection(ArrayList::new));
                        fn = () -> moveBotDiag(moveBotDiagArgs.get(0), moveBotDiagArgs.get(1), moveBotDiagArgs.get(2), moveBotDiagArgs.get(3));
                        break;
                    case "turnBot":
                        fn = () -> turnBot(Double.parseDouble(operationArgs.get(0)));
                        break;
                    case "liftBot":
                        if (operationArgs.size() == 2) {
                            fn = () -> liftBot(Integer.parseInt(operationArgs.get(0)), Double.parseDouble(operationArgs.get(1)));
                        } else {
                            fn = () -> liftBot(Integer.parseInt(operationArgs.get(0)));
                        }
                        break;

                    case "sleep":
                        fn = () -> sleep(Long.parseLong(operationArgs.get(0)));
                        break;

                    case "setPosition":
                        double servoPos = Double.parseDouble(operationArgs.get(1));
                        switch(operationArgs.get(0)) {
                            case "arm":
                                fn = () -> arm.setPosition(servoPos);
                                break;
                            case "claw":
                                fn = () -> claw.setPosition(servoPos);
                                break;
                        }
                        break;

                    case "setPower":
                        double powerFac = Double.parseDouble(operationArgs.get(1));
                        switch(operationArgs.get(0)) {
                            case "rf_drive":
                                fn = () -> rf_drive.setPower(powerFac);
                                break;
                            case "rb_drive":
                                fn = () -> rb_drive.setPower(powerFac);
                                break;
                            case "lf_drive":
                                fn = () -> lf_drive.setPower(powerFac);
                                break;
                            case "lb_drive":
                                fn = () -> lb_drive.setPower(powerFac);
                                break;
                            case "lift":
                                fn = () -> lift.setPower(powerFac);
                                break;
                        }
                        break;

                    case "moveServo":
                        /*
                        double servoPosParam = Double.parseDouble(operationArgs.get(1));
                        long servoSpeed = Long.parseLong(operationArgs.get(2));
                        switch(operationArgs.get(0)) {
                            case "arm":
                                fn = () -> moveServo(arm, servoPosParam, servoSpeed);
                                break;
                            case "claw":
                                fn = () -> moveServo(claw, servoPosParam, servoSpeed);
                                break;
                        }*/
                        double servoPosParam = Double.parseDouble(operationArgs.get(1));
                        switch(operationArgs.get(0)) {
                            case "arm":
                                fn = () -> moveServo(arm, servoPosParam);
                                break;
                            case "claw":
                                fn = () -> moveServo(claw, servoPosParam);
                                break;
                        }
                        break;

                    case "moveMotor":
                        int motorPos = Integer.parseInt(operationArgs.get(1));
                        double motorSpeed = Double.parseDouble(operationArgs.get(2));
                        boolean stay = operationArgs.size() > 3 && Boolean.parseBoolean(operationArgs.get(3));
                        switch(operationArgs.get(0)) {
                            case "rf_drive":
                                fn = () -> moveMotor(rf_drive, motorPos, motorSpeed, stay);
                                break;
                            case "rb_drive":
                                fn = () -> moveMotor(rb_drive, motorPos, motorSpeed, stay);
                                break;
                            case "lf_drive":
                                fn = () -> moveMotor(lf_drive, motorPos, motorSpeed, stay);
                                break;
                            case "lb_drive":
                                fn = () -> moveMotor(lb_drive, motorPos, motorSpeed, stay);
                                break;
                            case "lift":
                                fn = () -> moveMotor(lift, motorPos, motorSpeed, stay);
                                break;
                        }
                        break;
                    case "moveTillObjectSeen":
                        switch (operationArgs.size()) {
                            case 1:
                                fn = () -> moveTillObjectSeen(Boolean.parseBoolean(operationArgs.get(0)));
                                break;
                            case 2:
                                fn = () -> moveTillObjectSeen(Boolean.parseBoolean(operationArgs.get(0)), Boolean.parseBoolean(operationArgs.get(1)));
                                break;
                            case 3:
                                fn = () -> moveTillObjectSeen(Boolean.parseBoolean(operationArgs.get(0)), Boolean.parseBoolean(operationArgs.get(1)), Double.parseDouble(operationArgs.get(2)));
                        }
                        break;
                    case "powerFactor":
                        telemetry.addLine("CHANGING POWER FACTOR TO " + operationArgs.get(0));
                        telemetry.update();
                        this.powerFactor = Double.parseDouble(operationArgs.get(0));
                        telemetry.addLine(String.valueOf(this.powerFactor));
                        telemetry.update();
                        break;


                    case "placeSpecimen":
                        fn = () -> placeSpecimen();
                        break;
                    case "grabSample":
                        fn = () -> grabSample();
                        break;
                    case "sampleInBasket":
                        fn = () -> sampleInBasket();
                        break;
                    case "restLift":
                        fn = () -> restLift();
                        break;
                    case "topBasketLift":
                        fn = () -> topBasketLift();
                        break;
                    case "restArm":
                        fn = () -> restArm();
                        break;
                    case "goToSample":
                        fn = () -> goToSample();
                        break;

                    default:
//                        finalOperationArgs = operationArgs;
                        break;

                }
                if (fn != null) {
                    if (creatingLinearRunnable) {
                        fnsInFn.add(fn);
                    } else {
                        if (!fnsInFn.isEmpty()) {
                            // preserve fnsinfn for the runnable
                            List<Runnable> fnsInFn2 = new ArrayList<>(fnsInFn);
                            fn = () -> fnsInFn2.forEach(fnInFn -> CompletableFuture.runAsync(fnInFn).join());
                            fnsInFn.clear();
                        }
                        if (storeAsync) {
                            fns.add(fn);
                        } else {
                            if (!fns.isEmpty()) {
                                fns.add(fn);
                                runTasksAsync(fns);
                            } else {
                                CompletableFuture.runAsync(fn).join();
                            }
                            fns.clear();
                        }
                    }
                }



            }
            reader.close();


        } catch (Exception e) {
            telemetry.addLine("An error occurred:");
            telemetry.addLine(e.getMessage());
            telemetry.addLine(Stream.of(e.getStackTrace())
                            .map(String::valueOf)
                            .collect(Collectors.joining("\n")));
            telemetry.update();
            sleep(60000);
        }


    }






}
