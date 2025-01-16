package org.ucanakin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
  private static final String INDEX_DEFAULT_PATH = "index1";
  private static final String INDEX_EMBED_PATH = "index2";

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

  public static void run(String indexPath, boolean useEmbeddings) {
    try {
      IndexCreationService indexCreationService = new IndexCreationService(useEmbeddings);
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

      if (useEmbeddings) {
        System.out.println("Neural Method");
        System.out.println();
      } else {
        System.out.println("BM25 Method");
        System.out.println();
      }

      for (int k = 5; k <= 100; k+= 5) {
        System.out.print("Search Top ");
        System.out.println(k);

        if (useEmbeddings) {
          searchService.searchAllQueriesWithEmbeddings(indexPath, QUERY_FILES, relevanceMap, k);
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
    run( INDEX_DEFAULT_PATH,false);
    run( INDEX_EMBED_PATH,true);
  }
}