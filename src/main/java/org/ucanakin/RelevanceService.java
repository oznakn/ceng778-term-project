package org.ucanakin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelevanceService {

  public Map<Number, RelevanceObject> getRelevanceMap(List<String> filePaths, Map<String, Boolean> existingDocumentsMap) throws IOException {
    Map<Number, RelevanceObject> relevanceMap = new HashMap<>();
    for (String filePath: filePaths) {
      final File file = new File(filePath);
      relevanceMap.putAll(parse(file, existingDocumentsMap));
    }

    return relevanceMap;
  }

  private Map<Number, RelevanceObject> parse(final File file, Map<String, Boolean> existingDocumentsMap) throws IOException {
    Map<Number, RelevanceObject> relevanceMap = new HashMap<>();

    String content = new String(Files.readAllBytes(Paths.get(file.getPath())));

    String[] lines = content.split("\n");

    for (String line : lines) {
      line = line.replaceAll("\r", "");
      String[] parts = line.split(" ");
      Number queryId = Integer.parseInt(parts[0]);
      String docId = parts[2];
      String relevance = parts[3];

      if (!relevanceMap.containsKey(queryId)) {
        RelevanceObject relevanceObject = new RelevanceObject(queryId);
        relevanceMap.put(queryId, relevanceObject);
      }

      // we only have FT documents
      if (!docId.startsWith("FT")) {
        continue;
      }

      RelevanceObject relevanceObject = relevanceMap.get(queryId);
      relevanceObject.addRelevance(docId, relevance.equals("1"));
    }

    return relevanceMap;
  }

}
