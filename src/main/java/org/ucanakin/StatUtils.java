package org.ucanakin;

import java.util.ArrayList;
import java.util.List;

public class StatUtils {
  public static void printStats(List<ResultObject> results) {
    List<Double> precisions = new ArrayList<>();
    List<Double> recalls = new ArrayList<>();
    List<Double> times = new ArrayList<>();

    for (ResultObject result : results) {
      if (result.getPrecision() == null) {
        System.out.println("Zero Precision: " + result.getPrecision());
      }
      precisions.add(result.getPrecision());
      recalls.add(result.getRecall());
      times.add((double) result.getTime());
    }

    System.out.println("Precision@10 Mean: " + mean(precisions));
    System.out.println("Recall@10 Mean: " + mean(recalls));
    System.out.println("Time Mean (ms): " + mean(times) / 1000);

    System.out.println("Precision@10 Average: " + avg(precisions));
    System.out.println("Recall@10 Average: " + avg(recalls));
    System.out.println("Time Average (ms): " + avg(times) / 1000);
  }

  public static double mean(List<Double> values) {
    return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
  }

  public static double avg(List<Double> values) {
    return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
  }
}
