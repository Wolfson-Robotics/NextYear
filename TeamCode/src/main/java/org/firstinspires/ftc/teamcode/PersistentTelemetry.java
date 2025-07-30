package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PersistentTelemetry {

    private final Telemetry telemetry;
    private List<Supplier<String>> telemetryLines = new CopyOnWriteArrayList<>();

    public PersistentTelemetry(Telemetry telemetry, List<String> telemetryLines) {
        this.telemetry = telemetry;
        this.telemetryLines = telemetryLines.stream().map(s -> (Supplier<String>) () -> s).collect(Collectors.toList());
    }
    public PersistentTelemetry(Telemetry telemetry, String[] telemetryLines) {
        this(telemetry, Arrays.asList(telemetryLines));
    }
    public PersistentTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }


    private int findLine(String searchContent) {
        // iterate backwards because we want to modify most recent line not older ones
        searchContent = searchContent.trim().toLowerCase();
        for (int lineIndex = (this.telemetryLines.size() - 1); lineIndex >= 0; lineIndex--) {
            if (getLine(lineIndex).trim().toLowerCase().contains(searchContent)) {
                return lineIndex;
            }
        }
        return -1;
    }


    public void addLine(Supplier<String> line) {
        this.telemetryLines.add(line);
    }
    public void addLine(String content) {
        addLine(() -> content);
    }

    public void setLine(int lineIndex, Supplier<String> content) {
        this.telemetryLines.set(lineIndex, content);
    }
    public void setLine(int lineIndex, String content) {
        this.telemetryLines.set(lineIndex, () -> content);
    }
    public void setLine(String searchContent, Supplier<String> content) {
        int foundLine = findLine(searchContent);
        if (foundLine == -1) {
            this.addLine(content);
        } else {
            this.setLine(foundLine, content);
        }
    }
    public void setLine(String searchContent, String content) {
        this.setLine(searchContent, () -> content);
    }
    public void appendLine(int lineIndex, Supplier<String> content) {
        // Capture old Supplier to prevent recursion
        Supplier<String> oldLine = getLineRaw(lineIndex);
        this.telemetryLines.set(lineIndex, () -> oldLine.get() + content.get());
    }
    public void appendLine(int lineIndex, String content) {
        this.appendLine(lineIndex, () -> content);
    }
    public void appendLine(String searchContent, Supplier<String> content) {
        int foundLine = findLine(searchContent);
        if (foundLine == -1) {
            this.addLine(content);
        } else {
            this.appendLine(foundLine, content);
        }
    }
    public void appendLine(String searchContent, String content) {
        this.appendLine(searchContent, () -> content);
    }

    public void addData(String caption, String data) {
        addLine(caption + ": " + data);
    }
    public void addData(String caption, int data) {
        addLine(caption + ": " + data);
    }
    public void addData(String caption, double data) {
        addLine(caption + ": " + data);
    }
    public void addData(String caption, boolean data) {
        addLine(caption + ": " + data);
    }
    public void addData(String caption, Supplier<?> data) {
        addLine(() -> caption + ": " + data.get());
    }

    public void setData(String caption, String data) {
        setLine(caption, caption + ": " + data);
    }
    public void setData(String caption, int data) {
        setLine(caption, caption + ": " + data);
    }
    public void setData(String caption, double data) {
        setLine(caption, caption + ": " + data);
    }
    public void setData(String caption, boolean data) {
        setLine(caption, caption + ": " + data);
    }
    public void setData(String caption, Supplier<?> data) {
        setLine(caption, () -> caption + ": " + data.get());
    }

    private Supplier<String> getLineRaw(int lineIndex) {
        return this.telemetryLines.get(lineIndex);
    }
    public String getLine(int lineIndex) {
        return getLineRaw(lineIndex).get();
    }

    public void removeLine(String lineContent) {
        int foundLine = findLine(lineContent);
        if (foundLine != -1) this.telemetryLines.remove(foundLine);
    }

    public void update() {
        for (Supplier<String> line : telemetryLines) this.telemetry.addLine(line.get());
        this.telemetry.update();
    }
    public void clear() {
        this.telemetry.clear();
    }
}
