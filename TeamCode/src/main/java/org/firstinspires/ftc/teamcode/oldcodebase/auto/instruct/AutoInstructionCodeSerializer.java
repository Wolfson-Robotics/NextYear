package org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct;

import static org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct.AutoInstructionConstants.*;

import org.firstinspires.ftc.teamcode.oldcodebase.CustomTelemetryLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class AutoInstructionCodeSerializer {


    private static String getInput(String prompt) {
        Scanner sc = new Scanner(System.in);    //System.in is a standard input stream
        System.out.print(prompt);
        return sc.nextLine();
    }


    // This is to convert code instructions to text file instructions

    // READ THIS TO RUN THIS CODE:
    // TO RUN, CLICK THE GREEN TRIANGLE NEXT TO THE METHOD DECLARATION LINE,
    // THEN CLICK "RUN "AutoInstructionCodeSerializer" *WITH COVERAGE*.

    // To USE the program, you must enter a file path in the console, which can be accessed through
    // "Cover" at the bottom. You paste the file path ON YOUR COMPUTER and it will paste the code between
    // two lines of text: "BEGIN WRITING" and "END WRITING". You will most likely have to scroll up to see it.

    // Be sure to SET THE RUN CONFIGURATION BACK TO TEAMCODE WHEN REUPLOADING THE ACTUAL CODE.
    public static void main(String[] args) throws IOException {
        mainPipeline(getInput("Write outputs to input files?(Y/N):").equalsIgnoreCase("y"));
    }

    private static void mainPipeline(boolean writeSerialized) throws IOException {
        String inputPath = getInput("Input path of file:");
        LineNumberReader reader = new LineNumberReader(new FileReader(inputPath));
        String builtInstructions = AutoInstructionCodeSerializer.serialize(reader);

        System.out.println("\n\n\nBEGIN WRITING\n\n\n");
        System.out.println(builtInstructions);
        System.out.println("\n\n\nEND WRITING\n\n\n");
        if (writeSerialized) {
            CustomTelemetryLogger fileLogger = new CustomTelemetryLogger(inputPath);
            fileLogger.logData(builtInstructions);
            fileLogger.close();
        }

        mainPipeline(writeSerialized);
    }




    public static String serialize(BufferedReader codeInput) throws IOException {

        StringJoiner builtInstructions = new StringJoiner("\n");

        String rawLine;
        boolean storeAsync = false, creatingLinearRunnable = false;
        while ((rawLine = codeInput.readLine()) != null) {

            String filteredLine = rawLine.replaceAll("[\\r\\n\\t]", "").trim();
            filteredLine = filteredLine.endsWith(commaRegex) ? filteredLine.substring(0, filteredLine.length() - 1) : filteredLine;

            List<String> currBuiltInstruction = new ArrayList<>();

            // Checks for special operations
            if (rawLine.contains(singleCommentMarker)) {
//                rawLine = singleCommentMarker;
                builtInstructions.add(filteredLine);
                continue;
            }
            if (filteredLine.contains("() -> {")) {
                builtInstructions.add(new StringBuilder().append(storeAsync ? "\t" : "").append(linearRunnableMarker));
                creatingLinearRunnable = true;
                continue;
            }
            if (filteredLine.equals("}") && creatingLinearRunnable) {
                builtInstructions.add(new StringBuilder().append(storeAsync ? "\t" : "").append(endLinearRunnableMarker));
                creatingLinearRunnable = false;
                continue;
            }
            if (filteredLine.equals(");") && storeAsync) {
                builtInstructions.add(endThreadingMarker);
                storeAsync = false;
                continue;
            }
            

            // Text file instructions have the operation first then the arguments separated by a space
            // Code instructions split the function name (operation) and the function arguments by parentheses
            // So there will only be two elements in this array, assuming that the code was written well. For instance:
            // moveBot(1, 1, 0, 0); would result in operationSplit being equal to: [ "moveBot", "1, 1, 0, 0);" ]
            String[] operationSplit = (storeAsync ? (filteredLine
                    .replaceAll(Pattern.quote("()"), "")
                    .replaceAll("->", "")) : filteredLine)
                    .trim()
                    .split(openParenthesisRegex);


            String rawOperation = operationSplit[0];
            // We split the operation name with a period to support operations on servos and motors,
            // like "armServo.setPosition(0.55);"
            // NOTE: Also, hence forth, operations like these will just be referred to as servo operations,
            // even though they, as mentioned, work on motor operations as well.
            String[] operationCall = rawOperation.split(periodRegex);
            boolean isMechOperation = operationCall.length >= 2;


            // If the operation is a servo operation, then the operation (i.e. "setPosition") will be the last
            // element. If it's a regular operation (i.e. "moveBot") then it will be the last element again.
            // We can therefore condense this into just grabbing the last element of the array.
            String operationName = operationCall[operationCall.length - 1];
            String[] operationArgs;
            if (operationSplit.length > 1) {

                operationArgs = Stream.of(operationSplit[1].split(closeParenthesisRegex)[0].split(commaRegex))
                        .map(String::trim)
                        .toArray(String[]::new);

            } else {
                ArrayList<String> tempOperationArgs = new ArrayList<>(Arrays.asList(operationSplit[0].split(argJoinerRegex)));
                // Remove the first element of this as is in this case, the operation would be the first
                // element of the array, only separated from the args by a space
                tempOperationArgs.remove(0);
                operationArgs = tempOperationArgs.stream()
                        .map(String::trim)
                        .toArray(String[]::new);
            }



            switch(operationName) {
                case singleCommentMarker:
                case multiCommentBegin:
                case multiCommentEnd:
                    currBuiltInstruction.add(filteredLine);
                    break;
                case multiThreadingMarker:
                    builtInstructions.add(multiThreadingMarker);
                    storeAsync = true;
                    continue;
                default:
                    currBuiltInstruction.add(operationName);
                    currBuiltInstruction.add(joinArgsText(operationArgs));
                    break;
            }
            // The order of a servo operation in the text file goes like this:
            // setPosition armServo 0.12
            if (isMechOperation) {
                currBuiltInstruction.add(1, operationCall[0]);
            }

            builtInstructions.add(new StringBuilder().append(storeAsync ? "\t" : "").append(creatingLinearRunnable ? "\t" : "").append(joinArgsText(currBuiltInstruction).replaceAll(semicolonRegex, "")));


        }
        codeInput.close();

        return builtInstructions.toString();

    }

    public static String serialize(String codeInput) throws IOException {
        return AutoInstructionCodeSerializer.serialize(new BufferedReader(new StringReader(codeInput)));
    }

}