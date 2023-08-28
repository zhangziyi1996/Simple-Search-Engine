package SearchEngine;

import org.apache.lucene.search.ScoreDoc;

public class LuceneTester {

	/** Define the paths for the data file and the lucene index */
	//After following the general setup guide, here it is assumed that the dataset JSON file is within the same folder
	public static final String DATA_FILE="yelp_academic_dataset_review.json";
	public static final String INDEX_PATH="LuceneTest/luceneIndex";


	public static void main (String[] arg) throws Exception{

		boolean preformIndex=true;

		// To perform indexing. If there is no change to the data file, index only need to be created once

		if(preformIndex){
			QAIndexer indexer = new QAIndexer(LuceneTester.INDEX_PATH);
			indexer.indexQAs(LuceneTester.DATA_FILE);

		}


		//search index
		QASearcher searcher=new QASearcher(LuceneTester.INDEX_PATH);
		searcher.getCollectionSize();


		//search for search for text-typed fields, i.e., "text", "date".
		ScoreDoc[] hits=searcher.search("text", "really lovely place", 50);
		System.out.println("\n=================Results for text search=============\n");
		searcher.printResult(hits);

		//search for search for string-typed fields, i.e., "review_id", "user_id", "business_id".
		hits=searcher.stringSearch("user_id", "0ciI4_XNCye6I6bX_4JPLw", 50);
		System.out.println("\n=================Results for user_id search=============\n");
		searcher.printResult(hits);

		//search for number-typed fields, i.e., "stars", "useful", "funny", "cool".
		hits=searcher.numSearch("stars", 5, 30);
		System.out.println("\n=================Results for stars search=============\n");
		searcher.printResult(hits);
	}

}
