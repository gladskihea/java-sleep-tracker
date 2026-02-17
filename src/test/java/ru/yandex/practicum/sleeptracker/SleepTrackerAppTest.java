package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepTrackerAppTest {

    private final SleeplessNightsAnalysis analysis = new SleeplessNightsAnalysis();

    @Test
    void shouldReturnZeroIfSleepCoversNight() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepQuality.GOOD)
        );

        SleepAnalysisResult result = analysis.apply(sessions);
        assertEquals(0L, result.getValue(), "Должно быть 0 бессонных ночей");
    }

    @Test
    void shouldCountSleeplessNightIfSleepIsDuringDay() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 7, 0),
                        LocalDateTime.of(2025, 10, 1, 11, 0),
                        SleepQuality.GOOD)
        );

        SleepAnalysisResult result = analysis.apply(sessions);
        assertEquals(1L, result.getValue(), "Ночь должна считаться бессонной");
    }

    @Test
    void shouldNotCountAsSleeplessIfSleepIntersectsWindow() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 3, 0),
                        SleepQuality.GOOD)
        );

        SleepAnalysisResult result = analysis.apply(sessions);
        assertEquals(0L, result.getValue(), "Частичный сон ночью отменяет бессонницу");
    }

    @Test
    void shouldCountCorrectlyWithGapsBetweenDays() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 15, 0),
                        SleepQuality.NORMAL),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 0),
                        LocalDateTime.of(2025, 10, 4, 7, 0),
                        SleepQuality.GOOD)
        );

        SleepAnalysisResult result = analysis.apply(sessions);
        assertEquals(1L, result.getValue(), "Должна быть зафиксирована 1 бессонная ночь");
    }
}