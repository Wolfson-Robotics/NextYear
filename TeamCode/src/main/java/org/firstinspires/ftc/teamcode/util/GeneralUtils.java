package org.firstinspires.ftc.teamcode.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GeneralUtils {

    // Methods borrowed from https://github.com/Wolfson-Robotics/ColorSampleDetection/blob/main/src/main/java/org/wolfsonrobotics/colorsampledetection/util/conv/ImageConverter.java
    /**
     * Normalizes a value between a given minimum and maximum.
     */
    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    /**
     * Takes a normalized value and denormalizes it across a new minimum and maximum.
     */
    public static double denormalize(double normalized, double min, double max) {
        return (normalized * (max - min) + min);
    }

    /**
     * Directly converts a given value on a scale of a certain minimum and maximum to
     * a scale of a new, given minimum and maximum.
     */
    public static double convertScale(double value, double oldMin, double oldMax, double newMin, double newMax) {
        return denormalize(normalize(value, oldMin, oldMax), newMin, newMax);
    }



    protected void runTasksAsync(List<Runnable> fns) {
        ExecutorService executorService = Executors.newFixedThreadPool(fns.size()); // Thread pool
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        fns.forEach(fn -> futures.add(CompletableFuture.runAsync(fn, executorService)));

        CompletableFuture<Void> allThreads = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allThreads.join();
        executorService.shutdown();
    }
    protected void runTasksAsync(Runnable... fns) {
        runTasksAsync(Arrays.stream(fns).collect(Collectors.toList()));
    }

}
