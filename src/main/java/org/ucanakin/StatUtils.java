package org.ucanakin;

import java.util.ArrayList;
import java.util.List;

public class StatUtils {
  public static void printStats(List<ResultObject> results) {
    List<Double> precisions = new ArrayList<>();
    List<Double> recalls = new ArrayList<>();
    List<Double> ndcgs = new ArrayList<>();
    List<Double> times = new ArrayList<>();

    for (ResultObject result : results) {
      if (result.getPrecision() == null) {
        System.out.println("Zero Precision: " + result.getPrecision());
      }
      precisions.add(result.getPrecision());
      recalls.add(result.getRecall());
      ndcgs.add(result.getNdcg());
      times.add((double) result.getTime());
    }

    System.out.println("Precision Average: " + avg(precisions));
    System.out.println("Recall Average: " + avg(recalls));
    System.out.println("NDCG Average: " + avg(ndcgs));
    System.out.println("Time Average (ms): " + avg(times) / 1e6);
  }

  public static double median(List<Double> values) {
    values.sort(Double::compareTo);
    int size = values.size();
    if (size % 2 == 0) {
      return (values.get(size / 2 - 1) + values.get(size / 2)) / 2;
    } else {
      return values.get(size / 2);
    }
  }

  public static double avg(List<Double> values) {
    return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
  }
}
