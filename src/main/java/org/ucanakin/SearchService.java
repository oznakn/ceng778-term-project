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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.KnnFloatVectorQuery;
import org.apache.lucene.search.Query;
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

    org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(contentWithArray)));
    var rootDoc = doc.getFirstChild();
    for (int i = 0; i < rootDoc.getChildNodes().getLength(); i++) {
      queries.add(rootDoc.getChildNodes().item(i));
    }

    return queries;
  }

  public void searchAllQueries(String indexPath, List<String> filePaths, Map<Number, RelevanceObject> relevanceMap, int k)
      throws IOException, ParserConfigurationException, SAXException, ParseException {
    IndexReader reader = getReader(indexPath);
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());

    List<Node> queries = parseFiles(filePaths);
    List<ResultObject> results = new ArrayList<>();

    for (Node query: queries) {
      ResultObject result = searchQuery(searcher, query, relevanceMap, k);
      if (result != null) {
        results.add(result);
      }
    }

    StatUtils.printStats(results);
  }

  public void searchAllQueriesWithEmbeddings(String indexPath, List<String> filePaths, Map<Number, RelevanceObject> relevanceMap, int k)
          throws IOException, ParserConfigurationException, SAXException, ParseException {
    IndexReader reader = getReader(indexPath);
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());

    List<Node> queries = parseFiles(filePaths);
    List<ResultObject> results = new ArrayList<>();

    for (Node query: queries) {
      ResultObject result = searchQueryWithEmbeddings(searcher, query, relevanceMap, k);
      if (result != null) {
        results.add(result);
      }
    }

    StatUtils.printStats(results);
  }


  public static IndexReader getReader(String indexPath) throws IOException {
    return DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
  }

  public ResultObject searchQuery(IndexSearcher searcher, Node node, Map<Number, RelevanceObject> relevanceMap, int k)
      throws IOException, ParseException {
    if (node.getChildNodes().getLength() == 0) {
      return null;
    }

    Number queryId = Integer.parseInt(node.getChildNodes().item(1).getFirstChild().getNodeValue().replaceAll("[^0-9]", ""));
    String title = node.getChildNodes().item(3).getFirstChild().getNodeValue().replaceAll("\n", "").trim();

    title = title.replaceAll("/", "\\\\/");
    //System.out.println("Query: " + queryId + ": " + title);

    MultiFieldQueryParser parser = new MultiFieldQueryParser(
        new String[] {"TEXT"},
        IndexCreationService.getAnalyzer()
    );
    Query query = parser.parse(title);

    return new ResultObject(searcher, query, relevanceMap.get(queryId), k);
  }

  public ResultObject searchQueryWithEmbeddings(IndexSearcher searcher, Node node, Map<Number, RelevanceObject> relevanceMap, int k)
          throws IOException, ParseException {
    if (node.getChildNodes().getLength() == 0) {
      return null;
    }

    Number queryId = Integer.parseInt(node.getChildNodes().item(1).getFirstChild().getNodeValue().replaceAll("[^0-9]", ""));
    float[] embeddings = EmbeddingService.getInstance().getQueryEmbedding(queryId);
    KnnFloatVectorQuery query = new KnnFloatVectorQuery("EMBEDDING", embeddings, k);

    return new ResultObject(searcher, query, relevanceMap.get(queryId), k);
  }
}
