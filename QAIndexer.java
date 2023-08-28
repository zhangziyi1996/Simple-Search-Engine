package SearchEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.LineNumberReader;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class QAIndexer {

	private IndexWriter writer = null;

	//for recording time used for indexing
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    long startTime = System.nanoTime();

	public QAIndexer(String dir) throws IOException {
		//specify the directory to store the Lucene index
		Directory indexDir = FSDirectory.open(Paths.get(dir));

		//specify the analyzer used in indexing
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
		cfg.setOpenMode(OpenMode.CREATE);

		//create the IndexWriter
		writer = new IndexWriter(indexDir, cfg);
	}

	//specify what is a document, and how its fields are indexed
	protected Document getDocument(String text, long useful, String user_id, String date, String review_id, String business_id, long funny, long cool, long stars) throws Exception {
		Document doc = new Document();

		FieldType ft = new FieldType(TextField.TYPE_STORED);
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		ft.setStoreTermVectors(true);
		doc.add(new Field("text", text, ft));
		//ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);


		doc.add(new LongPoint("useful", useful));
		doc.add(new StoredField("useful", useful));
		doc.add(new StringField("user_id", user_id, Field.Store.YES));
		doc.add(new TextField("date", date, Field.Store.YES));
		doc.add(new StringField("review_id", review_id, Field.Store.YES));
		doc.add(new StringField("business_id", business_id, Field.Store.YES));
		doc.add(new LongPoint("funny", funny));
		doc.add(new StoredField("funny", funny));
		doc.add(new LongPoint("cool", cool));
		doc.add(new StoredField("cool", cool));
		doc.add(new LongPoint("stars", stars));
		doc.add(new StoredField("stars", stars));
		return doc;
	}


	public void indexQAs(String fileName) throws Exception {

		System.out.println("Start indexing "+fileName+" "+sdf.format(new Date()));
		//System.out.println("Start to work at" + startTime);

		//read a JSON file
		BufferedReader br = null;
	    JSONParser parser = new JSONParser();

		String sCurrentLine;
		br = new BufferedReader(new FileReader(fileName));


		int lineNumber = 1;

		//statistics on the total number of rows
		LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
		long lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		System.out.println("Total length of the file is " + lines);

		while ((sCurrentLine = br.readLine()) != null) {
			Object obj;
			try {
				obj = parser.parse(sCurrentLine);
				//parse the JSON file and extract the values for "question" and "answer"
				JSONObject jObj = (JSONObject) obj;
				String text = (String) jObj.get("text");
				long useful = (Long) jObj.get("useful");
				String user_id = (String) jObj.get("user_id");
				String date = (String) jObj.get("date");
				String review_id = (String) jObj.get("review_id");
				String business_id = (String) jObj.get("business_id");
				long funny = (Long) jObj.get("funny");
				long cool = (Long) jObj.get("cool");
				double stars_inter = (Double) jObj.get("stars");
				long stars = (long) stars_inter;

				//create a document for each JSON record
				Document doc=getDocument(text, useful, user_id, date, review_id, business_id, funny, cool, stars);

				//index the document
				writer.addDocument(doc);

				//report the processing time
				if (lineNumber % (lines/10) == 0) {
					long endTime   = System.nanoTime();
					long elapsedTime = endTime - startTime;
					double portion = ((double)lineNumber / (double)lines)*100;
			        long convert = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
					System.out.println("Time taken to index " + portion + "% data is " + convert + "seconds" );
				}

				lineNumber++;
			} catch (Exception e) {
				System.out.println("Error at: " + lineNumber + "\t" + sCurrentLine);
				e.printStackTrace();
			}
		}
		//close the file reader
		 try {
             if (br != null)br.close();
         } catch (IOException ex) {
             ex.printStackTrace();
         }
		System.out.println("Index completed at " + sdf.format(new Date()));

		//close the index writer.
		writer.close();

	}

}
