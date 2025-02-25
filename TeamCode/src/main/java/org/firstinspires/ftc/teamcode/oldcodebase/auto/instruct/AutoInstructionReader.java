package org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct;

import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AutoInstructionReader {

    // .txt filereader pipeline
    private LineNumberReader instructionsReader;

    private final String[] servoNames = new String[] { "arm", "claw" };
    private final String[] motorNames = new String[] { "rf_drive", "rb_drive", "lf_drive", "lb_drive", "lift" };
    private final String[] nicknames = new String[] {"bob", "joe", "marissa"};

    public AutoInstructionReader(File instructionsFile) throws FileNotFoundException {
        this.instructionsReader = new LineNumberReader(new FileReader(instructionsFile));
    }

    public AutoInstructionReader(String filePath) throws FileNotFoundException {
        this(new File(filePath));
    }


    // This function, hence the name, reads the next line in the file, then parses and validates it so that
    // it's ready for AutoJava to use accordingly
    public AutoOperation readLine() throws IOException {


        String rawLine = instructionsReader.readLine();
        if (rawLine == null) {
            return null;
        }


        String actualData = rawLine
                .replaceAll("\\r|\\n|\\t", "")
                .replaceAll((openParenthesisRegex + "|" + closeParenthesisRegex + "|" + semicolonRegex), " ")
                .trim();
        ArrayList<String> rawOperationArgs = new ArrayList<>(Arrays.asList(actualData.split(argJoinerRegex)));

        String originalOperationName = rawOperationArgs.get(0);
        String operationName = originalOperationName;
        // convenience operation finders (i.e. now you dont have to put a space in front of a comment indicator)
        if (operationName.startsWith(singleCommentMarker)) {
            operationName = singleCommentMarker;
        }
        // After getting the operation name, remove it from the array for it to stay true to its variable name
        rawOperationArgs.remove(0);


        rawOperationArgs = rawOperationArgs.stream()
                .map(String::trim)
                .filter(arg -> !arg.isEmpty()).collect(Collectors.toCollection(ArrayList::new));


        ArrayList<String> operationArgs = new ArrayList<>();
        switch (operationName) {

            case "moveBotOld":
            case "moveBotDiag":
            case "moveBot": {

                if (rawOperationArgs.size() != 4) {
                    throw new IOException("Incorrect number of parameters at " + operationName + " call at line " + this.getLineNumber());
                }
                for (String parameter : rawOperationArgs) {
                    if (!isDouble(parameter)) {
                        throw new IOException("Parameter " + parameter + " for " + operationName + " call is an invalid double at line " + this.getLineNumber());
                    }
                    operationArgs.add(parameter);
                }
                break;

            }
            case "turnBot": {

                if (rawOperationArgs.size() != 1) {
                    throw new IOException("Incorrect number of parameters at turnBot call at line " + this.getLineNumber());
                }
                if (!isDouble(rawOperationArgs.get(0))) {
                    throw new IOException("Parameter " + rawOperationArgs.get(0) + " for turnBot call is an invalid double at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                break;

            }
            case "liftBot": {

                if (rawOperationArgs.size() != 1 && rawOperationArgs.size() != 2) {
                    throw new IOException("Incorrect number of parameters at liftBot call at line " + this.getLineNumber());
                }
                if (!isInteger(rawOperationArgs.get(0))) {
                    throw new IOException("Parameter " + rawOperationArgs.get(0) + " for liftBot call is an invalid integer at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                if (rawOperationArgs.size() == 2) {
                    if (!isDouble(rawOperationArgs.get(1))) {
                        throw new IOException("Parameter " + rawOperationArgs.get(1) + " for liftBot call is an invalid double at line " + this.getLineNumber());
                    }
                    operationArgs.add(rawOperationArgs.get(1));
                }
                break;

            }
            case "sleep": {

                if (rawOperationArgs.size() != 1) {
                    throw new IOException("Incorrect number of parameters at sleep call at line " + this.getLineNumber());
                }
                if (!isInteger(rawOperationArgs.get(0))) {
                    throw new IOException("Parameter " + rawOperationArgs.get(0) + " is not a valid integer for sleep call at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                break;

            }
            case "setPower": {

                if (rawOperationArgs.size() != 2) {
                    throw new IOException("Incorrect number of parameters at " + operationName + " call at line " + this.getLineNumber());
                }
                if (!motorExists(rawOperationArgs.get(0))) {
                    throw new IOException("Motor " + rawOperationArgs.get(0) + " for " + operationName + " call does not exist at line " + this.getLineNumber());
                }
                if (!isDouble(rawOperationArgs.get(1))) {
                    throw new IOException("Motor power " + rawOperationArgs.get(1) + " is not a valid double for " + operationName + " call at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                operationArgs.add(rawOperationArgs.get(1));
                break;

            }
            case "setPosition": {

                if (rawOperationArgs.size() != 2) {
                    throw new IOException("Incorrect number of parameters at " + operationName + " call at line " + this.getLineNumber());
                }
                if (!servoExists(rawOperationArgs.get(0))) {
                    throw new IOException("Servo " + rawOperationArgs.get(0) + " for " + operationName + " call does not exist at line " + this.getLineNumber());
                }
                if (!isDouble(rawOperationArgs.get(1))) {
                    throw new IOException("Servo pos " + rawOperationArgs.get(1) + " is not a valid double for " + operationName + " call at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                operationArgs.add(rawOperationArgs.get(1));
                break;

            }
            case "moveServo": {
/*
                if (rawOperationArgs.size() != 3) {
                    throw new IOException("Incorrect number of parameters at " + operationName + " call at line " + this.getLineNumber());
                }
                if (!servoExists(rawOperationArgs.get(0))) {
                    throw new IOException("Servo " + rawOperationArgs.get(0) + " for " + operationName + " call does not exist at line " + this.getLineNumber());
                }
                if (!isDouble(rawOperationArgs.get(1))) {
                    throw new IOException("Servo pos " + rawOperationArgs.get(1) + " is not a valid double for " + operationName + " call at line " + this.getLineNumber());
                }
                if (!isLong(rawOperationArgs.get(2))) {
                    throw new IOException(("Servo speed " + rawOperationArgs.get(2) + " is not a valid long for " + operationName + " call at line " + this.getLineNumber()));
                }
                operationArgs.add(rawOperationArgs.get(0));
                operationArgs.add(rawOperationArgs.get(1));
                operationArgs.add(rawOperationArgs.get(2));*/
                if (rawOperationArgs.size() != 2) {
                    throw new IOException("Incorrect number of parameters at " + operationName + " call at line " + this.getLineNumber());
                }
                if (!servoExists(rawOperationArgs.get(0))) {
                    throw new IOException("Servo " + rawOperationArgs.get(0) + " for " + operationName + " call does not exist at line " + this.getLineNumber());
                }
                if (!isDouble(rawOperationArgs.get(1))) {
                    throw new IOException("Servo pos " + rawOperationArgs.get(1) + " is not a valid double for " + operationName + " call at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                operationArgs.add(rawOperationArgs.get(1));
                break;

            }
            case "moveMotor": {

                if (rawOperationArgs.size() != 3 && rawOperationArgs.size() != 4) {
                    throw new IOException("Incorrect number of parameters at " + operationName + " call at line " + this.getLineNumber());
                }
                if (!motorExists(rawOperationArgs.get(0))) {
                    throw new IOException("Motor " + rawOperationArgs.get(0) + " for " + operationName + " call does not exist at line " + this.getLineNumber());
                }
                if (!isInteger(rawOperationArgs.get(1))) {
                    throw new IOException("Motor target pos " + rawOperationArgs.get(1) + " is not a valid integer for " + operationName + " call at line " + this.getLineNumber());
                }
                if (!isDouble(rawOperationArgs.get(2))) {
                    throw new IOException(("Motor speed " + rawOperationArgs.get(2) + " is not a valid double for " + operationName + " call at line " + this.getLineNumber()));
                }
                if (rawOperationArgs.size() > 3 && !isBoolean(rawOperationArgs.get(3))) {
                    throw new IOException("Stay boolean " + rawOperationArgs.get(3) + " is not a valid boolean for " + operationName + " call at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                operationArgs.add(rawOperationArgs.get(1));
                operationArgs.add(rawOperationArgs.get(2));
                if (rawOperationArgs.size() > 4) {
                    operationArgs.add(rawOperationArgs.get(3));
                }
                break;

            }
            case "moveTillObjectSeen": {
                if (rawOperationArgs.size() != 1 && rawOperationArgs.size() != 2 && rawOperationArgs.size() != 3) {
                    throw new IOException("Incorrect number of parameters at " + operationName + " call at line " + this.getLineNumber());
                }
                if (!isBoolean(rawOperationArgs.get(0))) {
                    throw new IOException("Right boolean " + rawOperationArgs.get(1) + " is not a valid boolean for " + operationName + " call at line " + this.getLineNumber());
                }
                if (rawOperationArgs.size() > 1 && !isBoolean(rawOperationArgs.get(1))) {
                    throw new IOException("Timeout boolean " + rawOperationArgs.get(1) + " is not a valid boolean for " + operationName + " call at line " + this.getLineNumber());
                }
                if (rawOperationArgs.size() > 2 && !isDouble(rawOperationArgs.get(2))) {
                    throw new IOException("Threshold " + rawOperationArgs.get(2) + " is not a valid doube for " + operationName + " call at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                if (rawOperationArgs.size() > 1) {
                    operationArgs.add(rawOperationArgs.get(1));
                }
                if (rawOperationArgs.size() > 2) {
                    operationArgs.add(rawOperationArgs.get(2));
                }
                break;
            }
            case "powerFactor": {

                if (rawOperationArgs.size() != 1) {
                    throw new IOException("Incorrect number of parameters at " + operationName + " assignment at line " + this.getLineNumber());
                }
                if (!isDouble(rawOperationArgs.get(0))) {
                    throw new IOException(operationName + " assigned value " + rawOperationArgs.get(0) + " is not a valid double at line " + this.getLineNumber());
                }
                operationArgs.add(rawOperationArgs.get(0));
                break;

            }

            // We still want the whole text on the line of the comment
            // So we first append the "operation name" (in this case the comment indicator that
            // may contain snippets of code without a space separating it) to the operationArgs, which is
            // used in the auto instruction writer
            case singleCommentMarker:
                rawOperationArgs.add(0, originalOperationName);
                operationArgs = rawOperationArgs;
                break;
            case "":
            case multiCommentBegin:
            case multiCommentEnd:
            case stopMarker:
            case multiThreadingMarker:
            case endThreadingMarker:
            case linearRunnableMarker:
            case endLinearRunnableMarker:
            case "placeSpecimen":
            case "grabSample":
            case "sampleInBasket":
            case "restLift":
            case "topBasketLift":
            case "restArm":
            case "goToSample":
                // basic auto methods like restArm, trussArm, and tapePlace go here with these other cases
                break;
            default:
                throw new IOException("Unknown operation " + operationName + " with specific provided arguments at line " + this.getLineNumber());

        }

        return new AutoOperation(operationName, operationArgs);


    }


    private boolean isInteger(String integer) {
        try {
            Integer.parseInt(integer);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isDouble(String dbl) {
        try {
            Double.parseDouble(dbl);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isLong(String longStr) {
        try {
            Long.parseLong(longStr);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isBoolean(String booleanStr) {
        return booleanStr.toLowerCase().equals("true") || booleanStr.toLowerCase().equals("false");
    }





    private boolean servoExists(String servoName) {
        return Arrays.asList(this.servoNames).contains(servoName);
    }
    private boolean motorExists(String motorName) {
        return Arrays.asList(this.motorNames).contains(motorName);
    }


    public int getLineNumber() {
        return this.instructionsReader.getLineNumber();
    }


    public void close() throws IOException {
        this.instructionsReader.close();
    }



}