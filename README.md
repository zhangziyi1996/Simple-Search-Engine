# Simple-Search-Engine
 Development of a Simple Search Engine by using Apache Lucene   
### Dependent Libraries
<p align="justify">Two third-party libraries are needed for implementing the search engine: Apache Lucene library (Version 8.10.1): https://lucene.apache.org/ and JSON.simple (Version 1.1.1): https://code.google.com/archive/p/json-simple/.</p>

### Setup guide
<p align="justify">Firstly, add the above-mentioned two third-party libraries to your JAVA project build path. Then add the necessary Lucene JARs to the CLASSPATH. There are in total three of them: lucene-analyzers-common-8.10.1.jar, lucene-core-8.10.1.jar and lucene-queryparser-8.10.1.jar. </p>
<p align="justify">After that, depending on which field you want to search for, call different search methods from the QASearcher.java file. Specifically, call searcher.search() for searching text-typed fields. Call searcher.stringSearch for searching string-typed fields. Call searcher.numSearch for searching number-typed fields. (You may refer to the image below as an example.)</p>

![Picture1](https://github.com/zhangziyi1996/Simple-Search-Engine/assets/143377198/a7c165ee-76e6-4739-9b63-5f753ded4372)

<p align="justify">The query format and the number of available searching parameters are also shown in the image. Users should formulate their search query by inputting the name of the field, terms or keywords to search for and the maximum number of returned results respectively. The code searcher.printResult(hits) will display the searched results for you. </p>

### Explanations of sample output
<p align="justify">First of all, when the code is running for the first time, the indexer will process all the fields and create the inverted index table for them. During this process, detailed information such as the number of lines in the processing file, the time needed for indexing every 10% of the data will be shown in the IDE console window. </p>

![Picture2](https://github.com/zhangziyi1996/Simple-Search-Engine/assets/143377198/e32e6f70-6400-4d38-9e71-9d61fa0eea11)

<p align="justify">After that, when you pass in your desired search query into the system, for example, searching for 50 results of the phrase “really lovely place” in the “text” field, the following results will be shown in the console window:</p>

![Picture3](https://github.com/zhangziyi1996/Simple-Search-Engine/assets/143377198/9a8f3885-4da7-4930-a43f-10f9e539bc65)


<p align="justify">Arranged by the order of decreasing score, all 50 documents will be shown. A snippet which includes all the searchable fields and the document ID will be displayed for a quick view. For the top result, there will also be a special term frequency analysis, which explains well why it will get the highest score.</p>

![Picture4](https://github.com/zhangziyi1996/Simple-Search-Engine/assets/143377198/fa6cd4a0-eb1e-4561-a8db-0243b400a018)




