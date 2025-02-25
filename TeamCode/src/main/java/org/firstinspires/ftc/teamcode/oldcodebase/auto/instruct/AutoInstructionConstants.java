package org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct;

import java.util.List;
import java.util.regex.Pattern;

public class AutoInstructionConstants {


    // Base characters for parsing/serializing instructions from code to text and vica versa
    public static final String openParenthesis = "(";
    public static final String closeParenthesis = ")";
    public static final String semicolon = ";";
    public static final String period = ".";
    public static final String comma = ",";
    public static final String argJoiner = " ";


    // Regex versions of the base characters for things like splitting strings into arrays (of base arguments)
    public static final String openParenthesisRegex = Pattern.quote(openParenthesis);
    public static final String closeParenthesisRegex = Pattern.quote(closeParenthesis);
    public static final String semicolonRegex = Pattern.quote(semicolon);
    public static final String periodRegex = Pattern.quote(period);
    public static final String commaRegex = Pattern.quote(comma);
    public static final String argJoinerRegex = Pattern.quote(argJoiner);


    public static final String codeJoiner = comma + argJoiner;
    public static final String funcEnd = closeParenthesis + semicolon;


    public static final String codeJoinerRegex = Pattern.quote(codeJoiner);



    // Operations
    public static final String singleCommentMarker = "//";
    public static final String multiCommentBegin = "/*";
    public static final String multiCommentEnd = "*/";
    public static final String stopMarker = "STOP";
    public static final String multiThreadingMarker = "runTasksAsync";
    public static final String endThreadingMarker = "runTasksAsyncEnd";
    public static final String linearRunnableMarker = "directSuccession";
    public static final String endLinearRunnableMarker = "directSuccessionEnd";



    public static final String autoInstructPath = "/sdcard/autonomous/auto_instruct.txt";





    public static String joinArgsText(List<String> operationArgs) {
        return String.join(argJoiner, operationArgs);
    }

    public static String joinArgsText(String[] operationArgs) {
        return String.join(argJoiner, operationArgs);
    }


    public static String joinArgsCode(List<String> operationArgs) {
        return String.join(codeJoiner, operationArgs);
    }

    public static String joinArgsCode(String[] operationArgs) {
        return String.join(codeJoiner, operationArgs);
    }


}
