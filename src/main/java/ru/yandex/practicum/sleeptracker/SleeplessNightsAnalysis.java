package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import java.util.stream.LongStream;

public class SleeplessNightsAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }

        LocalDateTime firstSessionStart = sessions.get(0).getStart();
        LocalDateTime lastSessionEnd = sessions.get(sessions.size() - 1).getEnd();

        // Логика определения первой и последней ночи для анализа
        LocalDate startDate = firstSessionStart.toLocalTime().isBefore(LocalTime.NOON)
                ? firstSessionStart.toLocalDate().minusDays(1)
                : firstSessionStart.toLocalDate();

        LocalDate endDate = lastSessionEnd.toLocalDate();

        long totalDaysInRange = ChronoUnit.DAYS.between(startDate, endDate);

        long sleeplessNights = LongStream.range(0, totalDaysInRange)
                .mapToObj(startDate::plusDays)
                .filter(date -> sessions.stream().noneMatch(session -> {
                    LocalDateTime nightStart = date.plusDays(1).atStartOfDay();
                    LocalDateTime nightEnd = date.plusDays(1).atTime(6, 0);
                    return session.getStart().isBefore(nightEnd) && session.getEnd().isAfter(nightStart);
                }))
                .count();

        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessNights);
    }
}