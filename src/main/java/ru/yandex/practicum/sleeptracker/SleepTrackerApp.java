package ru.yandex.practicum.sleeptracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static void main(String[] args) {
        String fileName = (args.length > 0) ? args[0] : "sleep_log.txt";

        List<SleepingSession> sessions = loadSessions(fileName);

        if (sessions.isEmpty()) {
            System.out.println("Ошибка: не удалось прочитать данные из файла " + fileName);
            return;
        }

        List<Function<List<SleepingSession>, SleepAnalysisResult>> analyzers = new ArrayList<>();

        analyzers.add(new TotalSessionsAnalysis());
        analyzers.add(new MinDurationAnalysis());
        analyzers.add(new MaxDurationAnalysis());
        analyzers.add(new AverageDurationAnalysis());
        analyzers.add(new BadQualityAnalysis());
        analyzers.add(new SleeplessNightsAnalysis());
        analyzers.add(new ChronotypeAnalysis());
        analyzers.add(new SleeplessNightsAnalysis());
        analyzers.add(new ChronotypeAnalysis());

        // Запуск
        analyzers.stream()
                .map(analyzer -> analyzer.apply(sessions))
                .forEach(System.out::println);
    }

    private static List<SleepingSession> loadSessions(String fileName) {
        try (InputStream is = SleepTrackerApp.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) return List.of();

            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        String[] parts = line.split(";");
                        return new SleepingSession(
                                LocalDateTime.parse(parts[0], DT_FORMATTER),
                                LocalDateTime.parse(parts[1], DT_FORMATTER),
                                SleepQuality.valueOf(parts[2])
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
}