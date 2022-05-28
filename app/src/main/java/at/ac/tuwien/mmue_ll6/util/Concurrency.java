package at.ac.tuwien.mmue_ll6.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Concurrency class to handle several threads for saving scores
 * @author Michelle Lau
 */
public class Concurrency {
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public static void executeAsync(Runnable task) {
        executor.execute(task);
    }
}
