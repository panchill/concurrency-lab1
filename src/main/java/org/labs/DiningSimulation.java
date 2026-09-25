package org.labs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DiningSimulation {

    public static final int DEFAULT_MAX_LEAD = 100;

    private final int programmersCount;
    private final int foodCount;
    private final int waitersCount;
    private final int maxLead;

    public DiningSimulation(int programmersCount, int foodCount, int waitersCount) {
        this(programmersCount, foodCount, waitersCount, DEFAULT_MAX_LEAD);
    }

    public DiningSimulation(int programmersCount, int foodCount, int waitersCount, int maxLead) {
        if (programmersCount < 2) {
            throw new IllegalArgumentException("programmersCount must be >= 2");
        }
        if (foodCount < 0) {
            throw new IllegalArgumentException("foodCount must be >= 0");
        }
        if (waitersCount < 1) {
            throw new IllegalArgumentException("waitersCount must be >= 1");
        }
        if (maxLead < 1) {
            throw new IllegalArgumentException("maxLead must be >= 1");
        }
        this.programmersCount = programmersCount;
        this.foodCount = foodCount;
        this.waitersCount = waitersCount;
        this.maxLead = maxLead;
    }

    public int[] runTask() {
        Spoon[] spoons = new Spoon[programmersCount];
        for (int i = 0; i < programmersCount; i++) {
            spoons[i] = new Spoon(i);
        }

        FoodPool foodPool = new FoodPool(foodCount, programmersCount, maxLead);
        Waiter waiter = new Waiter(waitersCount, foodPool);

        Programmer[] programmers = new Programmer[programmersCount];
        for (int i = 0; i < programmersCount; i++) {
            Spoon left = spoons[i];
            Spoon right = spoons[(i + 1) % programmersCount];
            programmers[i] = new Programmer(i, left, right, waiter);
        }

        ExecutorService pool = Executors.newFixedThreadPool(programmersCount);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (Programmer p : programmers) {
                futures.add(pool.submit(p));
            }
            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Сбой в потоке программиста", e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Симуляция прервана", e);
                }
            }
        } finally {
            pool.shutdownNow();
        }

        int[] eatenFood = new int[programmersCount];
        for (int i = 0; i < programmersCount; i++) {
            eatenFood[i] = programmers[i].getMealsEaten();
        }
        return eatenFood;
    }
}