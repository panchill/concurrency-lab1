package org.labs;

import java.util.concurrent.Semaphore;

public class Waiter {

    private final Semaphore availableWaiters;
    private final FoodPool foodPool;

    public Waiter(int waitersCount, FoodPool foodPool) {
        if (waitersCount < 1) {
            throw new IllegalArgumentException("waitersCount must be >= 1");
        }
        this.availableWaiters = new Semaphore(waitersCount, true);
        this.foodPool = foodPool;
    }

    public boolean requestPortion() {
        availableWaiters.acquireUninterruptibly();
        try {
            return foodPool.takePortion();
        } finally {
            availableWaiters.release();
        }
    }
}