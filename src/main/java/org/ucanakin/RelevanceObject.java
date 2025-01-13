package org.ucanakin;

import java.util.HashMap;
import java.util.Map;

public class RelevanceObject {
  Map<String, Boolean> relevanceMap;
  Number queryId;
  int relevantDocCount;

  public RelevanceObject(Number queryId) {
    this.relevanceMap = new HashMap<>();
    this.queryId = queryId;
    relevantDocCount = 0;
  }

  public Map<String, Boolean> getRelevanceMap() {
    return relevanceMap;
  }

  public Number getQueryId() {
    return queryId;
  }

  public int getRelevantDocCount() {
    return relevantDocCount;
  }

  public void addRelevance(String docId, Boolean isRelevant) {
    relevanceMap.put(docId, isRelevant);
    if (isRelevant) {
      relevantDocCount += 1;
    }
  }
}
