package org.labs;

import java.util.concurrent.locks.ReentrantLock;

public class FoodPool {
    private final ReentrantLock lock = new ReentrantLock(true);
    private int remainingPortions;

    public FoodPool(int totalPortions) {
        if (totalPortions < 0) {
            throw new IllegalArgumentException("totalPortions must be >= 0");
        }
        this.remainingPortions = totalPortions;
    }

    public boolean takePortion() {
        lock.lock();
        try {
            if (remainingPortions <= 0) {
                return false;
            }
            remainingPortions--;
            return true;
        } finally {
            lock.unlock();
        }
    }
}
