package org.vpetrovych.exercises.streams;

import org.vpetrovych.exercises.streams.util.CourseResult;
import org.vpetrovych.exercises.streams.util.Person;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Collecting {

    public int sum(IntStream stream) {
        /* Your solution here */
        return 0;
    }

    public int production(IntStream stream) {
        /* Your solution here */
        return 0;
    }

    public int oddSum(IntStream stream) {
        /* Your solution here */
        return 0;
    }

    public Map<Integer, Integer> sumByRemainder(int div, IntStream stream) {
        /* Your solution here */
        return Collections.emptyMap();
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> courseResultStream) {
        /* Your solution here */
        return Collections.emptyMap();
    }

    private Set<String> getUniqueTaskNames(List<CourseResult> courseResultList) {
        /* Your solution here */
        return Collections.emptySet();
    }

    public double averageTotalScore(Stream<CourseResult> courseResultStream) {
        /* Your solution here */
        return 0;
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> courseResultStream) {
        /* Your solution here */
        return Collections.emptyMap();
    }

    public Map<Person, String> defineMarks(Stream<CourseResult> courseResultStream) {
        /* Your solution here */
        return Collections.emptyMap();
    }

    public String easiestTask(Stream<CourseResult> courseResultStream) {
        /* Your solution here */
        return "";
    }

    public Collector printableStringCollector() {
        /* Your solution here */
        return Collectors.toList();
    }
}