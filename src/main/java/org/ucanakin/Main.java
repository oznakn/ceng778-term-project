package org.ucanakin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

  static IndexCreationService indexCreationService = new IndexCreationService();
  static RelevanceService relevanceService = new RelevanceService();
  static SearchService searchService = new SearchService();

  private static final String INDEX_PATH = "index1";

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

  public static void main(String[] args) {
    //try {
    //  Bert bert = Bert.load("com/robrua/nlp/easy-bert/bert-uncased-L-12-H-768-A-12");
    //} catch (Exception e) {
    //  e.printStackTrace();
    //}

    try {
      // createIndex is used to create the index. If index is already created, you don't need to run this.
      //indexCreationService.createIndex(INDEX_PATH);

      // getRelevanceMap is used to get the relevance map from the relevance files.
      Map<Number, RelevanceObject> relevanceMap = relevanceService.getRelevanceMap(RELEVANCE_FILES);

      searchService.searchAllQuerties(INDEX_PATH, QUERY_FILES, relevanceMap);    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}