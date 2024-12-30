package org.ucanakin;

import java.nio.file.Paths;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

public class Main {

  static IndexCreationService indexCreationService = new IndexCreationService();
  private static final String INDEX_PATH = "index1";

  public static void main(String[] args) {
    //try {
    //  Bert bert = Bert.load("com/robrua/nlp/easy-bert/bert-uncased-L-12-H-768-A-12");
    //} catch (Exception e) {
    //  e.printStackTrace();
    //}

    try {
      // createIndex is used to create the index. If index is already created, you don't need to run this.
      indexCreationService.createIndex(INDEX_PATH);

      QueryParser parser = new QueryParser("TEXT", new StandardAnalyzer());
      Query query = parser.parse("International Organized Crime");

      IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH)));
      IndexSearcher searcher = new IndexSearcher(reader);
      searcher.setSimilarity(new BM25Similarity());

      TopDocs results = searcher.search(query, 10);
      StoredFields storedFields = searcher.storedFields();
      for (ScoreDoc scoreDoc : results.scoreDocs) {
        var doc = storedFields.document(scoreDoc.doc);
        System.out.println("Found: " + scoreDoc.score + " ::: " + doc.get("DOCNO"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}