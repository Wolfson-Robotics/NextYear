package org.firstinspires.ftc.teamcode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileLogger {

    private FileWriter writer;
    private int logNum = 1;

    private final File file;
    private final boolean append;


    private void initWriter() throws IOException {
        this.writer = new FileWriter(file, append);
    }

    public FileLogger(File file, boolean append) throws IOException {
        this.file = file;
        this.append = append;
        initWriter();
    }
    public FileLogger(File file) throws IOException {
        this(file, true);
    }
    public FileLogger(String filePath) throws IOException {
        this(filePath, true);
    }
    public FileLogger(String filePath, boolean append) throws IOException {
        this(new File(filePath), append);
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
    // The FileWriter handle tends to die over time, so it is re-instantiated in that case.
    public synchronized void logRaw(String data) throws IOException {
        try {
            writer.flush();
        } catch (IOException e) {
            initWriter();
        }
        writer.write(data);
        writer.flush();
    }

    public synchronized void close() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
