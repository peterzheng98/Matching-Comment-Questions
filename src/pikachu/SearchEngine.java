package pikachu;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.IOUtils.*;
import org.apache.lucene.util.Version;
import java.io.IOException;
import org.apache.lucene.search.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


public class SearchEngine {
    private ArrayList<String> rawText = null;

    public SearchEngine(ArrayList<String> rawText) {
        this.rawText = rawText;
    }

    public ArrayList<String> run(String targetText) throws IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer();
        ArrayList<String> ret = new ArrayList<>();

        Path indexPath = Files.createTempDirectory("tempIndex");
        Directory directory = FSDirectory.open(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        for(String __ : rawText) {
            String text = __;
            Document doc = new Document();
            doc.add(new Field("comment", text, TextField.TYPE_STORED));
            iwriter.addDocument(doc);
        }
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("comment", analyzer);
        Query query = parser.parse(targetText);
        ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            // System.out.println(hitDoc.get("comment"));
            ret.add(hitDoc.get("comment"));
        }
        ireader.close();
        directory.close();
        IOUtils.rm(indexPath);
        return ret;
    }
}
