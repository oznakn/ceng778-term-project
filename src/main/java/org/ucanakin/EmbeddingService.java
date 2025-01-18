package org.ucanakin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class EmbeddingService {
    static EmbeddingService getInstance(Boolean willCreateIndex, String model) {
        return new EmbeddingService(willCreateIndex, model);
    }

    private Map<String, ArrayList<Double>> queryEmbeddings;
    private Map<String, ArrayList<Double>> documentEmbeddings;

    EmbeddingService(Boolean willCreateIndex, String model) {
        try {
            // After
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if (willCreateIndex) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                documentEmbeddings = mapper.readValue(new FileReader("python/" + model + "-docs.json"), Map.class);
                System.out.println("Doc Embeddings loaded in " + stopwatch.stop());
            } else {
                Stopwatch stopwatch = Stopwatch.createStarted();
                queryEmbeddings = mapper.readValue(new FileReader("python/" + model + "-queries.json"), Map.class);
                System.out.println("Query Embeddings loaded in " + stopwatch.stop());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Map<String, float[]> getAllQueryEmbeddings() {
        return queryEmbeddings.entrySet().stream().collect(
            java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    double[] doubleQueryEmbedding = entry.getValue().stream().mapToDouble(Double::doubleValue).toArray();
                    float[] queryEmbedding = new float[doubleQueryEmbedding.length];
                    for (int i = 0; i < doubleQueryEmbedding.length; i++) {
                        queryEmbedding[i] = (float) doubleQueryEmbedding[i];
                    }
                    return queryEmbedding;
                }
            )
        );
    }

    Map<String, float[]> getAllDocumentEmbeddings() {
        return documentEmbeddings.entrySet().stream().collect(
            java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    double[] doubleDocumentEmbedding = entry.getValue().stream().mapToDouble(Double::doubleValue).toArray();
                    float[] documentEmbedding = new float[doubleDocumentEmbedding.length];
                    for (int i = 0; i < doubleDocumentEmbedding.length; i++) {
                        documentEmbedding[i] = (float) doubleDocumentEmbedding[i];
                    }
                    return documentEmbedding;
                }
            )
        );
    }
}
