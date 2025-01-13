package org.ucanakin;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SearchService {
  private List<Node> parseFiles(List<String> filePaths) throws IOException, ParserConfigurationException, SAXException {
    List<Node> queries = new ArrayList<>();
    for (String filePath: filePaths) {
      queries.addAll(parse(filePath));
    }

    return queries;
  }

  private List<Node> parse(String filePath) throws IOException, ParserConfigurationException, SAXException {
    ArrayList<Node> queries = new ArrayList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setIgnoringElementContentWhitespace(true);
    DocumentBuilder builder = factory.newDocumentBuilder();


    String content = new String(Files.readAllBytes(Paths.get(filePath)));

    // Apply magic to add missing end-tags
    content = content.replace("&", "&amp;").replace("\n", "").replaceAll("(?m)^(\\s*)<(?!(top))(\\w+)>([^<]+)$", "$1<$3>$4</$3>");

    String contentWithArray = "<root>" + content + "</root>";
    System.out.println("Indexing " + filePath);

    org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(contentWithArray)));
    var rootDoc = doc.getFirstChild();
    for (int i = 0; i < rootDoc.getChildNodes().getLength(); i++) {
      queries.add(rootDoc.getChildNodes().item(i));
    }

    return queries;
  }

  public void searchAllQuerties(String indexPath, List<String> filePaths, Map<Number, RelevanceObject> relevanceMap)
      throws IOException, ParserConfigurationException, SAXException, ParseException {
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());

    List<Node> queries = parseFiles(filePaths);

    for (Node query: queries) {
      searchQuery(searcher, query, relevanceMap);
    }
  }

  public void searchQuery(IndexSearcher searcher, Node node, Map<Number, RelevanceObject> relevanceMap)
      throws IOException, ParseException {
    if (node.getChildNodes().getLength() == 0) {
      return;
    }

    Number queryId = Integer.parseInt(node.getChildNodes().item(1).getFirstChild().getNodeValue().replaceAll("[^0-9]", ""));
    String title = node.getChildNodes().item(3).getFirstChild().getNodeValue().replaceAll("\n", "").trim();

    title = title.replaceAll("/", "\\\\/");
    System.out.println("Query: " + queryId + ": " + title);

    QueryParser parser = new QueryParser("TEXT", new StandardAnalyzer());
    Query query = parser.parse(title);

    TopDocs results = searcher.search(query, 10);
    StoredFields storedFields = searcher.storedFields();
    RelevanceObject relevanceObject = relevanceMap.get(queryId);
    int relevantCount = 0;

    for (ScoreDoc scoreDoc : results.scoreDocs) {
      var doc = storedFields.document(scoreDoc.doc);
      String docNo = doc.get("DOCNO");
      if (relevanceObject.getRelevanceMap().containsKey(docNo) && relevanceObject.getRelevanceMap().get(docNo)) {
        relevantCount += 1;
      }
      System.out.println("Found: " + scoreDoc.score + " ::: " + docNo  + " ::: " + relevanceObject.getRelevanceMap().get(docNo));
    }
    System.out.println("Recall: " + (float) relevantCount  / relevanceObject.getRelevantDocCount() + " ::: Precision: " + (float) relevantCount / 10);
  }
}
