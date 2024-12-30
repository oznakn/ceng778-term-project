package org.ucanakin;

import com.robrua.nlp.bert.Bert;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class Main {
    public static void main(String[] args) {
        try {
            Bert bert = Bert.load("com/robrua/nlp/easy-bert/bert-uncased-L-12-H-768-A-12");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String content = "hello world";

            Directory memoryIndex = new ByteBuffersDirectory();
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);
            Document document = new Document();

            document.add(new TextField("content", content, Field.Store.YES));

            writer.addDocument(document);
            writer.close();

            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query query = parser.parse("hello");
            IndexReader reader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(reader);

            TopDocs results = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                System.out.println("Found: " + scoreDoc.score);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}