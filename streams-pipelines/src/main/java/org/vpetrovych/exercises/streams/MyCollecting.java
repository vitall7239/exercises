package org.vpetrovych.exercises.streams;

import org.vpetrovych.exercises.streams.util.CourseResult;
import org.vpetrovych.exercises.streams.util.Person;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MyCollecting {

    public int sum(IntStream stream) {
        return stream.sum();
    }

    public int production(IntStream stream) {
        final int[] production = {1};
        stream.forEach(value -> production[0] = production[0] * value);
        return production[0];
    }

    public int oddSum(IntStream stream) {
        return stream.filter((n) -> ((n % 2) == 1) || ((n % 2) == -1))
                .sum();
    }

    public Map<Integer, Integer> sumByRemainder(int div, IntStream stream) {
        return stream.boxed()
                .collect(Collectors.groupingBy(s -> s % div, Collectors.summingInt(x -> x)));
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        Set<String> uniqueTaskNames = getUniqueTaskNames(courseResultList);
        return courseResultList.stream()
                .collect(Collectors.toMap(
                        CourseResult::getPerson,
                        courseResult -> uniqueTaskNames.stream()
                                .map(task -> Optional.ofNullable(courseResult.getTaskResults().get(task)).orElse(0))
                                .mapToDouble(el -> el)
                                .average()
                                .orElse(0)
                ));
    }

    private Set<String> getUniqueTaskNames(List<CourseResult> courseResultList) {
        return courseResultList.stream()
                .map(courseResult -> courseResult.getTaskResults().keySet())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public double averageTotalScore(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        Set<String> uniqueTaskNames = getUniqueTaskNames(courseResultList);
        return courseResultList.stream()
                .map(courseResult ->
                        uniqueTaskNames.stream()
                                .map(task -> Optional.ofNullable(courseResult.getTaskResults().get(task)).orElse(0))
                                .collect(Collectors.toList())
                )
                .flatMap(Collection::stream)
                .mapToDouble(el -> el)
                .average()
                .orElse(0);
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        Set<String> uniqueTaskNames = getUniqueTaskNames(courseResultList);
        return uniqueTaskNames.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        taskName -> courseResultList.stream()
                                .map(courseResult -> courseResult.getTaskResults().get(taskName) != null
                                        ? courseResult.getTaskResults().get(taskName)
                                        : 0
                                )
                                .mapToDouble(el -> el)
                                .average()
                                .orElse(0)
                ));
    }

    public Map<Person, String> defineMarks(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        Set<String> uniqueTaskNames = getUniqueTaskNames(courseResultList);
        return courseResultList.stream()
                .collect(Collectors.toMap(
                        CourseResult::getPerson,
                        courseResult -> numberToMark(
                                uniqueTaskNames.stream()
                                        .map(task -> Optional.ofNullable(courseResult.getTaskResults().get(task)).orElse(0))
                                        .mapToDouble(el -> el)
                                        .average()
                                        .orElse(0)))
                );
    }

    private String numberToMark(double number) {
        if (number > 90) {
            return "A";
        }
        if (number >= 83) {
            return "B";
        }
        if (number >= 75) {
            return "C";
        }
        if (number >= 68) {
            return "D";
        }
        if (number >= 60) {
            return "E";
        }
        return "F";
    }

    public String easiestTask(Stream<CourseResult> courseResultStream) {
        Map<String, Double> averageScoresPerTaskMap = averageScoresPerTask(courseResultStream);
        double maxValue = averageScoresPerTaskMap.values().stream()
                .max(Comparator.naturalOrder())
                .orElse(-1.0);
        return averageScoresPerTaskMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }

    public Collector printableStringCollector() {
        Supplier<Map<String, Map<String, Integer>>> supplier = HashMap::new;
        BiConsumer<Map<String, Map<String, Integer>>, CourseResult> accumulator = (acc, courseResult) -> {
            String key = courseResult.getPerson().getLastName() + " " + courseResult.getPerson().getFirstName();
            Map<String, Integer> taskMap = courseResult.getTaskResults();
            acc.put(key, taskMap);
        };
        BinaryOperator<Map<String, Map<String, Integer>>> combiner = (l, r) -> l;
        Function<Map<String, Map<String, Integer>>, String> finisher = (acc) -> {
            List<String> uniqueTaskNames = acc.values().stream()
                    .map(Map::keySet)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet())
                    .stream()
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toList());
            String[][] resultTable = createResultTable(uniqueTaskNames, acc.keySet().size());
            buildTable(resultTable, uniqueTaskNames, acc);
            return printTable(resultTable);
        };
        return Collector.of(supplier, accumulator, combiner, finisher);
    }

    private String[][] createResultTable(List<String> uniqueTaskNames, int personSize) {
        String[][] resultTable = new String[personSize + 2][uniqueTaskNames.size() + 3];
        resultTable[0][0] = "Student";
        for (int i = 0; i < uniqueTaskNames.size(); i++) {
            resultTable[0][i + 1] = uniqueTaskNames.get(i);
        }
        resultTable[0][uniqueTaskNames.size() + 1] = "Total";
        resultTable[0][uniqueTaskNames.size() + 2] = "Mark";
        return resultTable;
    }

    private void buildTable(String[][] resultTable,
                            List<String> orderedTaskNames,
                            Map<String, Map<String, Integer>> acc) {
        List<String> orderedPersonList = acc.keySet().stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        int orderedTaskNameSize = orderedTaskNames.size();
        for (int i = 0; i < orderedPersonList.size(); i++) {
            resultTable[i + 1][0] = orderedPersonList.get(i);
            Map<String, Integer> taskMap = acc.get(orderedPersonList.get(i));
            for (int j = 0; j < orderedTaskNameSize; j++) {
                resultTable[i + 1][j + 1] = Optional.ofNullable(taskMap.get(orderedTaskNames.get(j)))
                        .map(Object::toString)
                        .orElse("0");
            }
            double avarage = taskMap.values().stream()
                    .mapToDouble(el -> el)
                    .sum() / orderedTaskNameSize;
            BigDecimal avg = BigDecimal.valueOf(avarage).setScale(2, RoundingMode.HALF_DOWN);
            resultTable[i + 1][orderedTaskNameSize + 1] = avg.toString();
            resultTable[i + 1][orderedTaskNameSize + 2] = numberToMark(Double.parseDouble(resultTable[i + 1][orderedTaskNameSize + 1]));
        }
        buildAverage(resultTable, orderedPersonList.size(), orderedTaskNameSize);
    }

    private void buildAverage(String[][] resultTable, int personSize, int taskNameSize) {
        resultTable[personSize + 1][0] = "Average";
        BigDecimal avgSum = BigDecimal.ZERO;
        for (int t = 0; t < taskNameSize; t++) {
            double sum = 0;
            for (int p = 0; p < personSize; p++) {
                sum += Integer.parseInt(resultTable[p + 1][t + 1]);
            }
            BigDecimal avg = BigDecimal.valueOf(sum / personSize).setScale(2, RoundingMode.HALF_DOWN);
            avgSum = avgSum.add(avg);
            resultTable[personSize + 1][t + 1] = avg.toString();
        }
        BigDecimal avg = avgSum.divide(BigDecimal.valueOf(taskNameSize), 2, RoundingMode.HALF_DOWN);
        resultTable[personSize + 1][taskNameSize + 1] = avg.toString();
        resultTable[personSize + 1][taskNameSize + 2] = numberToMark(Double.parseDouble(resultTable[personSize + 1][taskNameSize + 1]));
    }

    private String printTable(String[][] resultTable) {
        int rowSize = resultTable.length;
        int colSize = resultTable[0].length;
        int[] columnMaxSize = new int[resultTable[0].length];
        for (int c = 0; c < colSize; c++) {
            int max = 0;
            for (String[] row : resultTable) {
                int length = row[c].length();
                if (length > max) {
                    max = length;
                }
            }
            columnMaxSize[c] = max;
        }
        StringBuilder result = new StringBuilder();
        for (int r = 0; r < rowSize; r++) {
            for (int c = 0; c < colSize; c++) {
                String text = resultTable[r][c];
                result.append(printColumn(text, columnMaxSize[c], c == 0));
            }
            if (rowSize - 1 != r) {
                result.append('\n');
            }
        }
        return result.toString();
    }

    private String printColumn(String text, int maxLength, boolean isFirstColumn) {
        int textLength = text.length();
        StringBuilder builder = new StringBuilder();
        if (isFirstColumn) {
            builder.append(text);
            generateSpaces(builder, maxLength - textLength);
        } else {
            builder.append(' ');
            generateSpaces(builder, maxLength - textLength);
            builder.append(text);
        }
        return builder.append(" |").toString();
    }

    private void generateSpaces(StringBuilder builder, int size) {
        builder.append(" ".repeat(Math.max(0, size)));
    }

}