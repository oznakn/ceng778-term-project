package org.ucanakin;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IndexCreationService {
  private final static String STOP_WORDS_FILENAME = "stopword.lst";
  private final static String DOCUMENTS_PATH = "ft/all";

  static StandardAnalyzer standardAnalyzer = new StandardAnalyzer(getStopWords());

  private boolean useEmbeddings = false;

  IndexCreationService(boolean useEmbeddings) {
    this.useEmbeddings = useEmbeddings;
  }

  public void createIndex(String indexPath) throws IOException, ParserConfigurationException, SAXException {
    StandardAnalyzer analyzer = getAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), indexWriterConfig);
    final File folder = new File(DOCUMENTS_PATH);
    addDocuments(folder, writer);

    writer.close();
  }

  public Map<String, Boolean> getAllExistingDocumentsMap() throws IOException, ParserConfigurationException, SAXException {
    final File folder = new File(DOCUMENTS_PATH);
    return getExistingDocumentsMap(folder);
  }

  public static StandardAnalyzer getAnalyzer() {
    return standardAnalyzer;
  }

  private Map<String, Boolean> getExistingDocumentsMap(File file)
      throws ParserConfigurationException, IOException, SAXException {
    Map<String, Boolean> existingDocumentsMap = new java.util.HashMap<>();

    if (file.isDirectory()) {
      for (final File fileEntry : Objects.requireNonNull(file.listFiles())) {
        existingDocumentsMap.putAll(getExistingDocumentsMap(fileEntry));
      }
      return existingDocumentsMap;
    }

    List<Document> documents = parse(file);

    for (Document document: documents) {
      existingDocumentsMap.put(document.get("DOCNO"), true);
    }

    return existingDocumentsMap;
  }

  private List<Document> parse(final File file) throws ParserConfigurationException, IOException, SAXException {
    String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
    return getDocumentsFromXML(content);
  }

  private List<Document> getDocumentsFromXML(final String content)
      throws ParserConfigurationException, IOException, SAXException {
    ArrayList<Document> documents = new ArrayList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setIgnoringElementContentWhitespace(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    String contentWithArray = "<root>" + content.replace("\n", "") + "</root>";

    org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(contentWithArray)));
    var rootDoc = doc.getFirstChild();
    for (int i = 0; i < rootDoc.getChildNodes().getLength(); i++) {
      Document document = new Document();
      var itemDoc = rootDoc.getChildNodes().item(i);
      String docNo = null;

      for (int j = 0; j < itemDoc.getChildNodes().getLength(); j++) {
        var propDoc = itemDoc.getChildNodes().item(j);
        TextField textField = new TextField(
                propDoc.getNodeName(),
                propDoc.getTextContent(),
                Field.Store.YES
        );
        document.add(textField);

        if (propDoc.getNodeName().equals("DOCNO")) {
          docNo = propDoc.getTextContent();
        }
      }

      if (useEmbeddings) {
        if (docNo == null) {
          System.out.println("Missing embedding!!!");
        } else {
          float[] embeddings = EmbeddingService.getInstance().getDocumentEmbedding(docNo);
          KnnFloatVectorField field = new KnnFloatVectorField("EMBEDDING", embeddings);
          document.add(field);
        }
      }

      documents.add(document);
    }

    return documents;
  }

  private void addDocument(final File file, IndexWriter writer)
      throws ParserConfigurationException, IOException, SAXException {

      if (file.getName().equals(STOP_WORDS_FILENAME)) {
        return;
      }

      List<Document> documents = parse(file);

      for (Document document: documents) {
        writer.addDocument(document);
      }
  }

  private void addDocuments(final File folder, IndexWriter writer)
      throws ParserConfigurationException, IOException, SAXException {
    for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
      if (fileEntry.isDirectory()) {
        addDocuments(fileEntry, writer);
      } else {
        addDocument(fileEntry, writer);
      }
    }
  }

  private static CharArraySet getStopWords() {
    String stopWordsPath = DOCUMENTS_PATH + "/" + STOP_WORDS_FILENAME;

    try {
      List<String> stopWords = Files
          .readAllLines(Paths.get(stopWordsPath))
          .stream()
          .filter(line -> !line.equals(""))
          .toList();
      return new CharArraySet(stopWords, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new CharArraySet(new ArrayList<>(), true);
  }
}
