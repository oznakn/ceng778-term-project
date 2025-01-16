package org.ucanakin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.*;

public class ResultObject {
  List<Boolean> relevanceList;
  Double precision;
  Double recall;
  Long time;

  public ResultObject(IndexSearcher searcher, Query query, RelevanceObject relevanceObject, int k) throws IOException {
    Long startTime = System.nanoTime();
    TopDocs results = searcher.search(query, k);
    Long endTime = System.nanoTime();

    time = endTime - startTime;

    StoredFields storedFields = searcher.storedFields();

    relevanceList = new ArrayList<>();

    if (results.scoreDocs.length == 0) {
        precision = 0.0;
        recall = 0.0;
        return;
    }

    for (ScoreDoc scoreDoc : results.scoreDocs) {
        var doc = storedFields.document(scoreDoc.doc);
        String docNo = doc.get("DOCNO");
        relevanceList.add(relevanceObject.getRelevanceMap().get(docNo));
    }

    precision = calculatePrecision();
    recall =
      relevanceObject.getRelevantDocCount() == 0 ? 1 : calculateRecall((double) relevanceObject.getRelevantDocCount());
  }

  public ResultObject(IndexSearcher searcher, KnnFloatVectorQuery query, RelevanceObject relevanceObject, int k) throws IOException {
    Long startTime = System.nanoTime();
    TopDocs results = searcher.search(query, k);
    Long endTime = System.nanoTime();

    time = endTime - startTime;

    StoredFields storedFields = searcher.storedFields();

    relevanceList = new ArrayList<>();

    if (results.scoreDocs.length == 0) {
      precision = 0.0;
      recall = 0.0;
      return;
    }

    for (ScoreDoc scoreDoc : results.scoreDocs) {
      var doc = storedFields.document(scoreDoc.doc);
      String docNo = doc.get("DOCNO");
      relevanceList.add(relevanceObject.getRelevanceMap().get(docNo));
    }

    precision = calculatePrecision();
    recall =
            relevanceObject.getRelevantDocCount() == 0 ? 1 : calculateRecall((double) relevanceObject.getRelevantDocCount());
  }

  private int getRelevantCount() {
    int relevantCount = 0;

    for (Boolean relevance : relevanceList) {
      if (relevance != null && relevance) {
        relevantCount += 1;
      }
    }

    return relevantCount;
  }

  private Double calculatePrecision() {
    return (double) getRelevantCount() / relevanceList.size();
  }

  private Double calculateRecall(Double relevantDocCount) {
    return (double) getRelevantCount() / relevantDocCount;
  }

  public List<Boolean> getRelevanceList() {
    return relevanceList;
  }

  public Double getPrecision() {
    return precision;
  }

  public Double getRecall() {
    return recall;
  }

  public Long getTime() {
    return time;
  }
}
