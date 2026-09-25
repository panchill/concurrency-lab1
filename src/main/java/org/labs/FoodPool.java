package org.labs;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FoodPool {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition queueMoved = lock.newCondition();
    private final int[] served;
    private final int maxLead;
    private int remainingPortions;
    private int minServed = 0;
    private int countAtMin;

    public FoodPool(int totalPortions, int programmersCount, int maxLead) {
        if (totalPortions < 0) {
            throw new IllegalArgumentException("totalPortions must be >= 0");
        }
        if (programmersCount < 1) {
            throw new IllegalArgumentException("programmersCount must be >= 1");
        }
        if (maxLead < 1) {
            throw new IllegalArgumentException("maxLead must be >= 1");
        }
        this.remainingPortions = totalPortions;
        this.served = new int[programmersCount];
        this.maxLead = maxLead;
        this.countAtMin = programmersCount;
    }

    public boolean awaitTurn(int programmerId) throws InterruptedException {
        lock.lock();
        try {
            while (remainingPortions > 0 && served[programmerId] >= minServed + maxLead) {
                queueMoved.await();
            }
            return remainingPortions > 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean takePortion(int programmerId) {
        lock.lock();
        try {
            if (remainingPortions <= 0) {
                return false;
            }
            if (served[programmerId] >= minServed + maxLead) {
                throw new IllegalStateException("takePortion() вызван без awaitTurn()");
            }

            remainingPortions--;
            int before = served[programmerId]++;

            boolean minChanged = false;
            if (before == minServed) {
                countAtMin--;
                if (countAtMin == 0) {
                    minServed++;
                    countAtMin = countServedEqualTo(minServed);
                    minChanged = true;
                }
            }

            if (minChanged || remainingPortions == 0) {
                queueMoved.signalAll();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private int countServedEqualTo(int value) {
        int count = 0;
        for (int s : served) {
            if (s == value) {
                count++;
            }
        }
        return count;
    }
}