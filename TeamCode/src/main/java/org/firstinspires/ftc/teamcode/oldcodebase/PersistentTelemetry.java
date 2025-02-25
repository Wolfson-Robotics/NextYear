package org.firstinspires.ftc.teamcode.oldcodebase;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.Arrays;

public class PersistentTelemetry {

    private Telemetry telemetry;
    private ArrayList<String> telemetryLines = new ArrayList<>();

    public PersistentTelemetry(Telemetry telemetry, ArrayList<String> telemetryLines) {
        this.telemetry = telemetry;
        this.telemetryLines = telemetryLines;
    }
    public PersistentTelemetry(Telemetry telemetry, String[] telemetryLines) {
        this(telemetry, new ArrayList<>(Arrays.asList(telemetryLines)));
    }
    public PersistentTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }


    private int findLine(String searchContent) {
        // iterate backwards because we want to modify most recent line not older ones
        searchContent = searchContent.trim().toLowerCase();
        for (int lineIndex = (this.telemetryLines.size() - 1); lineIndex >= 0; lineIndex--) {
            if (this.telemetryLines.get(lineIndex).trim().toLowerCase().contains(searchContent)) {
                return lineIndex;
            }
        }
        return -1;
    }


    public void addLine(String content) {
        this.telemetryLines.add(content);
    }

    public void setLine(int lineIndex, String content) {
        this.telemetryLines.set(lineIndex, content);
    }
    public void setLine(String searchContent, String content) {
        int foundLine = findLine(searchContent);
        if (foundLine == -1) {
            this.addLine(content);
        } else {
            this.setLine(foundLine, content);
        }
    }

    public String getLine(int lineIndex) {
        return this.telemetryLines.get(lineIndex);
    }

    public void addData(String caption, int data) {
        addLine(caption + ": " + data);
    }
    public void addData(String caption, double data) {
        addLine(caption + ": " + data);
    }

    public void setData(String caption, int data) {
        setLine(caption, caption + ": " + data);
    }
    public void setData(String caption, double data) {
        setLine(caption, caption + ": " + data);
    }

    public void removeLine(String lineContent) {
        int foundLine = findLine(lineContent);
        if (foundLine != -1) this.telemetryLines.remove(foundLine);
    }


    public void update() {
        this.telemetryLines.forEach(telemetryLine -> this.telemetry.addLine(telemetryLine));
        this.telemetry.update();
    }
    public void clear() {
        this.telemetry.clear();
    }
}
