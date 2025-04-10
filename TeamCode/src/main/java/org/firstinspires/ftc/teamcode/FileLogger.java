package org.firstinspires.ftc.teamcode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileLogger {

    private final FileWriter writer;
    private int logNum = 1;

    public FileLogger(String filePath) throws IOException {
        this(filePath, true);
    }

    public FileLogger(String filePath, boolean append) throws IOException {
        this.writer = new FileWriter(filePath, append);
    }

    public synchronized void logData(String data) throws IOException {
        logRaw(data + "\n");
    }
    public synchronized void logData(List<String> data) throws IOException {
        for (String dat : data) {
            this.logData(dat);
        }
    }
    public synchronized void logDataNum(String data) throws IOException {
        logData(logNum + ": " + data);
        logNum++;
    }
    public synchronized void logRaw(String data) throws IOException {
        writer.write(data);
    }

    public synchronized void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
