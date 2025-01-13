package org.ucanakin;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IndexCreationService {
  private final static String STOP_WORDS_FILENAME = "stopword.lst";
  private final static String DOCUMENTS_PATH = "ft/all";

  public void createIndex(String indexPath) throws IOException, ParserConfigurationException, SAXException {
    StandardAnalyzer analyzer = new StandardAnalyzer(getStopWords());
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), indexWriterConfig);
    final File folder = new File(DOCUMENTS_PATH);
    addDocuments(folder, writer);

    writer.close();
  }

  private List<Document> parse(final File file) throws ParserConfigurationException, IOException, SAXException {
    ArrayList<Document> documents = new ArrayList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setIgnoringElementContentWhitespace(true);
    DocumentBuilder builder = factory.newDocumentBuilder();


    String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
    String contentWithArray = "<root>" + content.replace("\n", "") + "</root>";
    System.out.println("Indexing " + file.getPath());

    org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(contentWithArray)));
    var rootDoc = doc.getFirstChild();
    for (int i = 0; i < rootDoc.getChildNodes().getLength(); i++) {
      Document document = new Document();
      var itemDoc = rootDoc.getChildNodes().item(i);
      for (int j = 0; j < itemDoc.getChildNodes().getLength(); j++) {
        var propDoc = itemDoc.getChildNodes().item(j);
        TextField textField = new TextField(
            propDoc.getNodeName(),
            propDoc.getTextContent(),
            Field.Store.YES
        );
        document.add(textField);
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

  private CharArraySet getStopWords() {
    String stopWordsPath = DOCUMENTS_PATH + STOP_WORDS_FILENAME;

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
