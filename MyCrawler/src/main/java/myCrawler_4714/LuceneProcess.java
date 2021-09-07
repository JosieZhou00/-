/*
 * @(#)MyCrawle.java         21/01/12
 *
 * created by Siying Zhou
 * All rights reserved.
 *
 * This program is used as a crawler towards dangdang.com to get structured information,
 * store it in a JSON file, create index and search book information by keywords. 
 */

package myCrawler_4714;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * This class is used to process the data, create index and search by index.
 *
 * @version 	1.0  12 Jan 2021
 * @author 	    Siying Zhou
 */
public class LuceneProcess {
/* use "createIndex" to create index, "getDocument" to get documents by book information, "search" to search by index. */
	
	/**
     * This function is to initialize, creating index storage directory and index.
     */
	public static void main(String[] args) {
		LuceneProcess w=new LuceneProcess();
		String filePath="data/index";//Create index storage directory
		w.createIndex(filePath);//Create index
		System.out.println("Index created successfully!");
	}
	
	/**
     * This function is to create index storage directory and index.
     */
	public void createIndex(String filePath){
		File f=new File(filePath);
		IndexWriter iwr=null;
		try {
			Directory dir=FSDirectory.open(f);
			Analyzer analyzer = new IKAnalyzer();
			IndexWriterConfig conf=new IndexWriterConfig(Version.LUCENE_4_10_0,analyzer);
			iwr=new IndexWriter(dir,conf);//create IndexWriter
			
			File file = new File("book.json");
			ArrayList<JSONObject> json=new ArrayList<JSONObject>();
			JSONObject obj;
		    
		    // This will reference one line at a time
		    String line = null;
			
			try
		    {
				 FileInputStream fis = new FileInputStream(file); 
			     InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
			     BufferedReader br = new BufferedReader(isr); 

	             while((line = br.readLine()) != null) {
	                obj = (JSONObject) new JSONParser().parse(line);
	                json.add(obj);
	             }
	            
	             // Always close files.
	             br.close();
	            
		    }catch (Exception e) {
				// TODO: handle exception
			}
			
			int recordNum = getLineNumber(file);
			for(int i=0;i<recordNum-1;i++) {
				Document doc=getDocument(json,i);	
				iwr.addDocument(doc);//Add doc, Lucene search is based on document as the basic unit
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			iwr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
     * This function is to get document by the JSON array and the line number.
     */
	public Document getDocument(ArrayList<JSONObject> json,int lineNum){
		
		//doc中内容由field构成，在检索过程中，Lucene会按照指定的Field依次搜索每个document的该项field是否符合要求。
		//The content of the document is composed of Field. During the retrieval process,
		//Lucene will search each document according to the specified domain for this field to meet the requirements.
		Document doc=new Document();

		try
	    {
			 
            JSONObject jsonObject = json.get(lineNum) ;
            
            //test code
//            System.out.println(jsonObject.toJSONString());
            
            Book book = new Book();
           
            book.setTitle(jsonObject.get("标题").toString());
            book.setAuthor(jsonObject.get("作者").toString());
            book.setType(jsonObject.get("分类").toString());
            book.setPress(jsonObject.get("出版社").toString());
            book.setCurrentPrice(jsonObject.get("价格").toString());
            book.setPicInfo(jsonObject.get("图书照片").toString());
            book.setEditorsChoice(jsonObject.get("编辑推荐").toString());
            book.setContent(jsonObject.get("内容简介").toString());
            book.setAuthorIntroduction(jsonObject.get("作者简介").toString());
            book.setCatalog(jsonObject.get("目录").toString());
            book.setUrl(jsonObject.get("url").toString());
            book.setHeadTitle(jsonObject.get("副标题").toString());
            
    		Field f1=new TextField("title",book.getTitle(),Field.Store.YES);
    		Field f2=new TextField("author",book.getAuthor(),Field.Store.YES);
    		Field f3=new TextField("type",book.getType(),Field.Store.YES);
    		Field f4=new TextField("press",book.getPress(),Field.Store.YES);
    		Field f5=new TextField("currentPrice",book.getCurrentPrice(),Field.Store.YES);
    		Field f6=new TextField("picInfo",book.getPicInfo(),Field.Store.YES);
    		Field f7=new TextField("editorsChoice",book.getEditorsChoice(),Field.Store.YES);
    		Field f8=new TextField("content",book.getContent(),Field.Store.YES);
    		Field f9=new TextField("authorIntro",book.getAuthorIntroduction(),Field.Store.YES);
    		Field f10=new TextField("catalog",book.getCatalog(),Field.Store.YES);
    		Field f11=new TextField("url",book.getUrl(),Field.Store.YES);
    		Field f12=new TextField("headTitle",book.getHeadTitle(),Field.Store.YES);
    		
    		doc.add(f1);
    		doc.add(f2);
    		doc.add(f3);
    		doc.add(f4);
    		doc.add(f5);
    		doc.add(f6);
    		doc.add(f7);
    		doc.add(f8);
    		doc.add(f9);
    		doc.add(f10);
    		doc.add(f11);
    		doc.add(f12);
    		
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
		
		return doc;
	}
	
	/**
     * This function is to search book information by index.
     */
	public boolean search(String filePath, String queryStr, String index){
		File f=new File(filePath);
		try {
			IndexSearcher searcher=new IndexSearcher(DirectoryReader.open(FSDirectory.open(f)));
			
			Analyzer analyzer = new IKAnalyzer();
			
			//指定field为index，Lucene会按照关键词搜索每个doc中的index。
			//Specify the field as index, Lucene will search for the name in each doc according to keywords.

			QueryParser parser = new QueryParser(Version.LUCENE_4_10_0, index, analyzer);
			
			Query query=parser.parse(QueryParser.escape(queryStr));
			TopDocs hits=searcher.search(query,1);//output 1 record
//			System.out.println("查询结果的总条数："+ hits.totalHits);
			if(hits.totalHits==0) {
				return false;
			}
			
			for(ScoreDoc doc:hits.scoreDocs){
				Document d=searcher.doc(doc.doc);

				System.out.println("书名："+d.get("title"));
				System.out.println("作者："+d.get("author"));
				System.out.println("类型："+d.get("type"));
				System.out.println("出版社："+d.get("press"));
				System.out.println("价格："+d.get("currentPrice"));
				System.out.println("图片信息："+d.get("picInfo"));
				System.out.println("副标题："+d.get("headTitle"));
				System.out.println("编辑推荐："+d.get("editorsChoice"));
				System.out.println("内容简介："+d.get("content"));
				System.out.println("作者信息："+d.get("authorIntro"));
				System.out.println("目录："+d.get("catalog"));
				System.out.println("图书链接："+d.get("url"));

			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
     * This function is to get the line number of a file.
     */
	public int getLineNumber(File file) {
	    if (file.exists()) {
	        try {
	            FileReader fileReader = new FileReader(file);
	            LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
	            lineNumberReader.skip(Long.MAX_VALUE);
	            int lines = lineNumberReader.getLineNumber() + 1;
	            fileReader.close();
	            lineNumberReader.close();
	            return lines;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return 0;
	} 

}
