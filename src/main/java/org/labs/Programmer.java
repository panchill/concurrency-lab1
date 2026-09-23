package org.labs;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Programmer implements Runnable {

    private final int id;
    private final Spoon firstSpoon;
    private final Spoon secondSpoon;
    private final Waiter waiter;
    private final CyclicBarrier startGate;
    private final AtomicInteger mealsEaten = new AtomicInteger(0);

    public Programmer(int id, Spoon leftSpoon, Spoon rightSpoon, Waiter waiter, CyclicBarrier startGate) {
        this.id = id;
        this.waiter = waiter;
        this.startGate = startGate;

        if (leftSpoon.getId() < rightSpoon.getId()) {
            this.firstSpoon = leftSpoon;
            this.secondSpoon = rightSpoon;
        } else {
            this.firstSpoon = rightSpoon;
            this.secondSpoon = leftSpoon;
        }
    }

    @Override
    public void run() {
        try {
            startGate.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch (BrokenBarrierException e) {
            return;
        }
        while (waiter.requestPortion()) {
            eat();
        }
    }

    private void eat() {
        firstSpoon.lock();
        try {
            secondSpoon.lock();
            try {
                mealsEaten.incrementAndGet();
            } finally {
                secondSpoon.unlock();
            }
        } finally {
            firstSpoon.unlock();
        }
    }

    public int getMealsEaten() {
        return mealsEaten.get();
    }
}