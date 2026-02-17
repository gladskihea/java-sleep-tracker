package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        Map<String, Long> counts = sessions.stream()
                .filter(s -> s.getStart().toLocalTime().isAfter(LocalTime.of(17, 0)) ||
                        s.getEnd().toLocalTime().isBefore(LocalTime.of(11, 0)))
                .map(this::getNightType)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long owls = counts.getOrDefault("Сова", 0L);
        long larks = counts.getOrDefault("Жаворонок", 0L);
        long pigeons = counts.getOrDefault("Голубь", 0L);

        String winner;
        if (owls > larks && owls > pigeons) {
            winner = "Сова";
        } else if (larks > owls && larks > pigeons) {
            winner = "Жаворонок";
        } else {
            winner = "Голубь";
        }

        return new SleepAnalysisResult("Ваш хронотип", winner);
    }

    private String getNightType(SleepingSession s) {
        LocalTime bed = s.getStart().toLocalTime();
        LocalTime wake = s.getEnd().toLocalTime();

        if (bed.isAfter(LocalTime.of(23, 0)) && wake.isAfter(LocalTime.of(9, 0))) {
            return "Сова";
        } else if (bed.isBefore(LocalTime.of(22, 0)) && wake.isBefore(LocalTime.of(7, 0))) {
            return "Жаворонок";
        } else {
            return "Голубь";
        }
    }
}