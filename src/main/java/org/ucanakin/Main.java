package org.ucanakin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
  private static final String BM25 = "BM25";
  private static final String MODEL_1 = "all-mpnet-base-v2";
  private static final String MODEL_2 = "all-MiniLM-L6-v2";

  private static final List<String> RELEVANCE_FILES = new ArrayList<>(
      List.of(
          "query-relJudgments/qrel_301-350_complete.txt",
          "query-relJudgments/qrels.trec7.adhoc_350-400.txt",
          "query-relJudgments/qrels.trec8.adhoc.parts1-5_400-450"
      )
  );

  private static final List<String> QUERY_FILES = new ArrayList<>(
      List.of(
          "query-relJudgments/q-topics-org-SET1.txt",
          "query-relJudgments/q-topics-org-SET2.txt",
          "query-relJudgments/q-topics-org-SET3.txt"
             )
  );

  public static void run(String indexPath, String model) {
    try {
      IndexCreationService indexCreationService = new IndexCreationService(model);
      RelevanceService relevanceService = new RelevanceService();
      SearchService searchService = new SearchService();

      File indexFile = new File(indexPath);
      if (!indexFile.exists()) {
        // createIndex is used to create the index. If index is already created, you don't need to run this.
        indexCreationService.createIndex(indexPath);
      }

      // getRelevanceMap is used to get the relevance map from the relevance files.
      Map<String, Boolean> existingDocumentsMap = indexCreationService.getAllExistingDocumentsMap();
      Map<Number, RelevanceObject> relevanceMap = relevanceService.getRelevanceMap(RELEVANCE_FILES, existingDocumentsMap);

      System.out.println("---------------------------------");
      if (model != null) {
        System.out.println("Neural Method - " + model);
      } else {
        System.out.println("BM25 Method");
      }
      System.out.println("---------------------------------");
      System.out.println();

      for (int k = 5; k <= 50; k+= 5) {
        System.out.println("Search Top " + k + ": ");

        if (model != null) {
          searchService.searchAllQueriesWithEmbeddings(indexPath, relevanceMap, k, model);
        } else {
          searchService.searchAllQueries(indexPath, QUERY_FILES, relevanceMap, k);
        }
        System.out.println();
      }
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    run("index-" + BM25, null);
    run("index-" + MODEL_1, MODEL_1);
    run("index-" + MODEL_2, MODEL_2);
  }
}