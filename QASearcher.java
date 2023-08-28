package SearchEngine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.LongPoint;

public class QASearcher {

	long startTime = System.nanoTime();

	private IndexSearcher lSearcher;
	private IndexReader lReader;

	public QASearcher(String dir) {

		try {
			//create an index reader and index searcher
			lReader = DirectoryReader.open(FSDirectory.open(Paths.get(dir)));
			lSearcher = new IndexSearcher(lReader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//report the number of documents indexed
	public void getCollectionSize() {
		System.out.println("Total number of documents indexed: " + this.lReader.numDocs());
	}

	//search for keywords in specified field, with the number of top results
	public ScoreDoc[] search(String field, String keywords, int numHits) {

		//the query has to be analyzed the same way as the documents being index
		//using the same Analyzer
		QueryBuilder builder = new QueryBuilder(new StandardAnalyzer());
		Query query = builder.createBooleanQuery(field, keywords);
		ScoreDoc[] hits = null;
		try {
			//Create a TopScoreDocCollector
			TopScoreDocCollector collector = TopScoreDocCollector.create(numHits, numHits);

			//search index
			lSearcher.search(query, collector);

			//collect results
			hits = collector.topDocs().scoreDocs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hits;
	}


	public Query toNumQuery(String field, long value)  {
		  return LongPoint.newExactQuery(field, value);
		}

	public ScoreDoc[] numSearch(String field, long value, int numHits) throws IOException, ParseException {
		TopDocs found = lSearcher.search(toNumQuery(field, value), numHits);
		ScoreDoc[] hits = found.scoreDocs;
		return hits;
	}

	//public ScoreDoc[] stringSearch(String id, int numHits) throws IOException, ParseException {
		//Query query = new TermQuery(new Term("zVqfBNUS9cTyZZicbzKdqg"));
		//TopDocs found = lSearcher.search(query, 10);
		//ScoreDoc[] hits = found.scoreDocs;
		//return hits;
	//}

	public ScoreDoc[] stringSearch(String field, String keywords, int numHits) {

		//the query has to be analyzed the same way as the documents being index
		//using the same Analyzer
		QueryBuilder builder = new QueryBuilder(new KeywordAnalyzer());
		Query query = builder.createBooleanQuery(field, keywords);
		ScoreDoc[] hits = null;
		try {
			//Create a TopScoreDocCollector
			TopScoreDocCollector collector = TopScoreDocCollector.create(numHits, numHits);

			//search index
			lSearcher.search(query, collector);

			//collect results
			hits = collector.topDocs().scoreDocs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hits;
	}

	//present the search results
	public void printResult(ScoreDoc[] hits) throws Exception {
		System.out.println("Found " + hits.length + " hits.");
		int i = 1;
		for (ScoreDoc hit : hits) {
			System.out.println("\nResult " + i + "\tDocID: " + hit.doc + "\t Score: " + hit.score);
			try {
				System.out.println("text: " + lReader.document(hit.doc).get("text"));
				System.out.println("user_id: " + lReader.document(hit.doc).get("user_id"));
				System.out.println("useful: " + lReader.document(hit.doc).get("useful"));
				System.out.println("date: " + lReader.document(hit.doc).get("date"));
				System.out.println("review_id: " + lReader.document(hit.doc).get("review_id"));
				System.out.println("business_id: " + lReader.document(hit.doc).get("business_id"));
				System.out.println("funny: " + lReader.document(hit.doc).get("funny"));
				System.out.println("cool: " + lReader.document(hit.doc).get("cool"));
				System.out.println("stars: " + lReader.document(hit.doc).get("stars"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			//print out more extra information for the top search result

			if(i==1) {
				Terms terms=getTermVector(hit.doc, "text");
				System.out.println("doc: "+hit.doc);

				TermsEnum iterator = terms.iterator();
				BytesRef term = null;
				System.out.print("List of Terms: ");
				while ((term = iterator.next()) != null) {
					String termText = term.utf8ToString();
     			    long termFreq = iterator.totalTermFreq(); // term freq in doc with docID
     			    System.out.print(termText+":"+termFreq+"\t");
				}
				System.out.println();
			}
			i++;

		}

		long endTime   = System.nanoTime();
		long elapsedTime = endTime - startTime;
        long convert = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
		System.out.println("Time taken to process the search query is " + convert + " milliseconds" );
	}


	//get term vector
	public Terms getTermVector (int docID, String field) throws Exception {
		return lReader.getTermVector(docID, field);
	}

	public void close() {
		try {
			if (lReader != null) {
				lReader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
