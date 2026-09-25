package org.labs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Timeout(value = 90, unit = TimeUnit.SECONDS)
@DisplayName("Тесты симуляции обеда программистов")
class DiningSimulationTest {

    private static final int DEFAULT_PROGRAMMERS_COUNT = 7;
    private static final int DEFAULT_FOOD_COUNT = 1_000_000;
    private static final int DEFAULT_WAITERS_COUNT = 2;

    private static final double ALLOWED_DEVIATION = 0.02;

    private int spread(int[] eatenFood) {
        return Arrays.stream(eatenFood).max().orElseThrow() - Arrays.stream(eatenFood).min().orElseThrow();
    }

    private double getMinMaxDeviation(int programmersCount, int foodCount, int[] eatenFood) {
        if (foodCount == 0) return 0;
        double mean = (double) foodCount / programmersCount;
        return spread(eatenFood) / mean;
    }

    @Test
    @DisplayName("Запуск со стандартными значениями")
    void runTask_shouldCompleteWithDefaultParameters() {
        DiningSimulation task = new DiningSimulation(DEFAULT_PROGRAMMERS_COUNT, DEFAULT_FOOD_COUNT, DEFAULT_WAITERS_COUNT);

        int[] eatenFood = task.runTask();

        assertThat(Arrays.stream(eatenFood).sum())
                .isEqualTo(DEFAULT_FOOD_COUNT);
        assertThat(spread(eatenFood))
                .isLessThanOrEqualTo(DiningSimulation.DEFAULT_MAX_LEAD);
        assertThat(getMinMaxDeviation(DEFAULT_PROGRAMMERS_COUNT, DEFAULT_FOOD_COUNT, eatenFood))
                .isLessThan(ALLOWED_DEVIATION);
    }

    @ParameterizedTest(name = "Число программистов {0}, число еды {1} и число официантов {2}")
    @CsvSource({
            "2,1000000,2",
            "3,1000000,10",
            "4,100000,2",
            "6,2000000,2",
            "7,1000000,20",
            "7,1000000,200",
            "7,2000000,2000",
    })
    @DisplayName("Разброс не превышает допустимый отрыв при любом N и M")
    void runTask_spreadNeverExceedsMaxLead(int programmersCount, int foodCount, int waitersCount) {
        DiningSimulation task = new DiningSimulation(programmersCount, foodCount, waitersCount);

        int[] eatenFood = task.runTask();

        assertThat(Arrays.stream(eatenFood).sum())
                .isEqualTo(foodCount);
        assertThat(spread(eatenFood))
                .isLessThanOrEqualTo(DiningSimulation.DEFAULT_MAX_LEAD);
    }

    @ParameterizedTest(name = "Число программистов {0}, число еды {1}, официантов {2}, допустимый отрыв {3}")
    @CsvSource({
            "7,100000,2,1",
            "3,100000,1,1",
            "2,100000,1,1",
    })
    @DisplayName("Разброс не превышает заданный отрыв")
    void runTask_spreadNeverExceedsCustomMaxLead(int programmersCount, int foodCount, int waitersCount, int maxLead) {
        DiningSimulation task = new DiningSimulation(programmersCount, foodCount, waitersCount, maxLead);

        int[] eatenFood = task.runTask();

        assertThat(Arrays.stream(eatenFood).sum())
                .isEqualTo(foodCount);
        assertThat(spread(eatenFood))
                .isLessThanOrEqualTo(maxLead);
    }

    @ParameterizedTest(name = "Число программистов {0}, число еды {1} и число официантов {2}")
    @CsvSource({
            "2,1000000,1",
            "7,0,2",
            "7,1,2",
            "7,3,2",
            "7,1000000,1",
    })
    @DisplayName("Задача с граничными аргументами")
    void runTask_shouldCompleteWithBoundaryParameters(int programmersCount, int foodCount, int waitersCount) {
        DiningSimulation task = new DiningSimulation(programmersCount, foodCount, waitersCount);

        int[] eatenFood = task.runTask();

        assertThat(Arrays.stream(eatenFood).sum())
                .isEqualTo(foodCount);
        assertThat(spread(eatenFood))
                .isLessThanOrEqualTo(DiningSimulation.DEFAULT_MAX_LEAD);
    }

    @ParameterizedTest(name = "Число программистов {0}, число еды {1} и число официантов {2}")
    @CsvSource({
            "1,1000000,2",
            "0,1000000,2",
            "7,-1,1",
            "7,1000000,0"
    })
    @DisplayName("Задача с невалидными аргументами")
    void diningSimulation_shouldThrowExceptionWithInvalidParameters(int programmersCount, int foodCount, int waitersCount) {
        assertThatThrownBy(() -> new DiningSimulation(programmersCount, foodCount, waitersCount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Недопустимый отрыв (меньше 1)")
    void diningSimulation_shouldThrowExceptionWithInvalidMaxLead() {
        assertThatThrownBy(() -> new DiningSimulation(7, 1000, 2, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}