package org.labs;

import java.util.concurrent.Semaphore;

public class Waiter {

    private final Semaphore availableWaiters;
    private final FoodPool foodPool;

    public Waiter(int waitersCount, FoodPool foodPool) {
        if (waitersCount < 1) {
            throw new IllegalArgumentException("waitersCount must be >= 1");
        }
        this.availableWaiters = new Semaphore(waitersCount);
        this.foodPool = foodPool;
    }

    public boolean requestPortion(int programmerId) throws InterruptedException {
        if (!foodPool.awaitTurn(programmerId)) {
            return false;
        }
        availableWaiters.acquireUninterruptibly();
        try {
            return foodPool.takePortion(programmerId);
        } finally {
            availableWaiters.release();
        }
    }
}