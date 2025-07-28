package org.firstinspires.ftc.teamcode.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Async {

    public static void runTasksAsync(List<Runnable> fns) {
        ExecutorService executorService = Executors.newFixedThreadPool(fns.size());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        fns.forEach(fn -> futures.add(CompletableFuture.runAsync(fn, executorService)));

        CompletableFuture<Void> allThreads = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allThreads.join();
        executorService.shutdown();
    }
    public static void runTasksAsync(Runnable... fns) {
        runTasksAsync(Arrays.stream(fns).collect(Collectors.toList()));
    }

    public static void async(Runnable fn) {
        new Thread(fn).start();
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {}
    }

}
