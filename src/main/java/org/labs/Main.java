package org.labs;

public class Main {

    private static final int DEFAULT_PROGRAMMERS = 2;
    private static final int DEFAULT_FOOD = 1_000_000;
    private static final int DEFAULT_WAITERS = 1;

    public static void main(String[] args) {
        int programmersCount = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PROGRAMMERS;
        int totalFood = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_FOOD;
        int waitersCount = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_WAITERS;
        int maxLead = args.length > 3 ? Integer.parseInt(args[3]) : DiningSimulation.DEFAULT_MAX_LEAD;

        int[] eatenFood = new DiningSimulation(programmersCount, totalFood, waitersCount, maxLead).runTask();

        printStatistics(eatenFood, totalFood);
    }

    private static void printStatistics(int[] eatenFood, int totalFood) {
        int sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < eatenFood.length; i++) {
            int meals = eatenFood[i];
            sum += meals;
            min = Math.min(min, meals);
            max = Math.max(max, meals);
            System.out.printf("Программист %d поел %d раз%n", i, meals);
        }

        System.out.printf("Итого съедено: %d из %d%n", sum, totalFood);
        System.out.printf("Разброс (max-min): %d%n", max - min);
    }
}