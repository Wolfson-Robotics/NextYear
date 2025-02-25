package org.firstinspires.ftc.teamcode.oldcodebase.auto.instruct;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class AutoOperation {
    private final String operationName;
    private final ArrayList<String> operationArgs;

    public AutoOperation(String operationName, ArrayList<String> operationArgs) {
        this.operationName = operationName;
        this.operationArgs = operationArgs;
    }

    public String getOperationName() {
        return this.operationName;
    }
    public ArrayList<String> getOperationArgs() {
        return this.operationArgs;
    }
}