package org.ucanakin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class EmbeddingService {
    private static EmbeddingService instance;

    static EmbeddingService getInstance() {
        if (instance == null) {
            instance = new EmbeddingService();
        }
        return instance;
    }

    private JsonObject queryEmbeddings;
    private JsonObject documentEmbeddings;

    EmbeddingService() {
        try {
            queryEmbeddings = (JsonObject) new JsonParser().parse(new FileReader("python/qembeddings.json"));
            documentEmbeddings = (JsonObject) new JsonParser().parse(new FileReader("python/embeddings.json"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    float[] getQueryEmbedding(Number queryId) {
        JsonArray array = queryEmbeddings.get(queryId.toString()).getAsJsonArray();
        float[] embeddings = new float[array.size()];
        for (int i = 0; i < embeddings.length; i++) {
            embeddings[i] = array.get(i).getAsFloat();
        }
        return embeddings;
    }

    float[] getDocumentEmbedding(String docNo) {
        JsonArray array = documentEmbeddings.get(docNo).getAsJsonArray();
        float[] embeddings = new float[array.size()];
        for (int i = 0; i < embeddings.length; i++) {
            embeddings[i] = array.get(i).getAsFloat();
        }
        return embeddings;
    }
}
