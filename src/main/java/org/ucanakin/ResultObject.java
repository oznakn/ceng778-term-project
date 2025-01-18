package org.ucanakin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.*;
import com.google.common.base.Stopwatch;

public class ResultObject {
  List<Boolean> relevanceList;
  Double precision;
  Double recall;
  Long time;
  Double ndcg;

  public ResultObject(IndexSearcher searcher, Query query, RelevanceObject relevanceObject, int k) throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    TopDocs results = searcher.search(query, k);
    time = stopwatch.elapsed().toNanos();
    calculateAndSaveStats(searcher, results, relevanceObject);
  }

  public ResultObject(IndexSearcher searcher, KnnFloatVectorQuery query, RelevanceObject relevanceObject, int k) throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    TopDocs results = searcher.search(query, k);
    time = stopwatch.elapsed().toNanos();
    calculateAndSaveStats(searcher, results, relevanceObject);
  }

  private void calculateAndSaveStats(IndexSearcher searcher, TopDocs results, RelevanceObject relevanceObject)
      throws IOException {
    StoredFields storedFields = searcher.storedFields();

    relevanceList = new ArrayList<>();

    if (results.scoreDocs.length == 0) {
      precision = 0.0;
      recall = 0.0;
      ndcg = 0.0;
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
    ndcg = calculateNdcg();
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

  public Double calculateNdcg() {
    double dcg = 0.0;
    double idcg = 0.0;
    int i = 0;
    for (Boolean relevance : relevanceList) {
      i++;
      if (relevance != null && relevance) {
        dcg += 1 / Math.log(i + 1);
      }
      idcg += 1 / Math.log(i + 1);
    }
    return dcg / idcg;
  }

  public List<Boolean> getRelevanceList() {
    return relevanceList;
  }

  public Double getNdcg() {
    return ndcg;
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
