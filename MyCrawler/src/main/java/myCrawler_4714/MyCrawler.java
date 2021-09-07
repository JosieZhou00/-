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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * This class is used to set the crawler rules, and output the information to a JSON file.
 *
 * @version 	1.0  12 Jan 2021
 * @author 	    Siying Zhou
 */
public class MyCrawler extends WebCrawler {
/* use "visit" to crawl the Web, "decodeUnicode" and "JsonWrite" to output the information. */
	
    /**
     * This function is to decide whether the given url should be crawled.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url){
       	//get the url and turn it to the lower case
    	String href = url.getURL().toLowerCase();
    	
        // Only accept the url if it is in the "http://product.dangdang.com" or "http://book.dangdang.com" domain
        // and protocol is "http".
        boolean b =href.startsWith("http://book.dangdang.com") || href.startsWith("http://product.dangdang.com") ;
        return b;
    }
    
    /**
     * This function is to parse the fetched page and store the processed data.
     */
    @Override
    public void visit(Page page){
    	Book book = new Book();
        String url = page.getWebURL().getURL();
        
        //Determine whether the page is a real page
        if (page.getParseData() instanceof HtmlParseData && url.startsWith("http://product.dangdang.com")) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();            
           
            //the content of the parsed page html
            String html = htmlParseData.getHtml();
            
            //parse the html to a document 
            Document doc= Jsoup.parse(html);
            
/**
 * Information is needed including title, author, type, press, picture information, 
 * editor's choice, content, author's introduction, catalog, price, and head title.        
 */
            //get title
            Element bookname=doc.getElementsByTag("h1").first();
        	String bntext=bookname.text();
        	book.setTitle(bntext);
 
        	//get author
            Elements author = doc.getElementsByAttributeValue("dd_name", "作者");
        	String artext=author.text().substring(3);//remove"作者："
        	artext=artext.substring(artext.length()/2+1);//Remove the duplicate author name
        	book.setAuthor(artext);
        	
            //get type
            Elements type = doc.getElementsByAttributeValue("dd_name", "详情所属分类");          
        	String tptext=type.text().substring(5);
        	book.setType(tptext);
        	
            //get press
            Elements press = doc.getElementsByAttributeValue("dd_name", "出版社");
        	String pstext=press.text().substring(4);//先去除"出版社："
        	pstext=pstext.substring(pstext.length()/2+1);//Remove the duplicate press name
        	book.setPress(pstext);
        	
            //get book picture url
            Elements pic_info = doc.getElementsByAttributeValue("dd_name", "大图");           
            Elements pic = pic_info.select("img[src$=.jpg]");  
        	String[] picstrm = pic.outerHtml().split("\"");
        	String pictext = picstrm[5];
        	book.setPicInfo(pictext);
        	
        	//get price
            Element currentPrice=doc.getElementById("dd-price");
           	String cptext=currentPrice.text().substring(1);//Remove the ￥ symbol, because it cannot be displayed correctly
           	book.setCurrentPrice(cptext);
           	
            //get head title
            Elements head_title_name=doc.getElementsByClass("head_title_name");//副标题
        	String hdntext=head_title_name.text();
        	book.setHeadTitle(hdntext);
        	
        	//get url
        	String[] cutURL = url.split("html");
        	book.setUrl(cutURL[0]+"html");
        	
        	//get the AJAX(Asynchronous JavaScript and XML) request url
            StringBuffer ajaxRequestUrl = new StringBuffer("http://product.dangdang.com/index.php?r=callback%2Fdetail&");
        	Elements ajaxScript = doc.getElementsByTag("script").eq(1);
        	for(Element e : ajaxScript) {
        		String[] varstrm = e.data().toString().split("var");
        		
        		//the second fragment contains the data we need
        		String var = varstrm[2];
        		if(var.contains("prodSpuInfo")) {
        			String[] datastrm = var.split("=");
        			String requestData = datastrm[1];
        			try {
        				JSONObject obj = new JSONObject(requestData);
        				String productId = obj.getString("productId");
        				String templateType = obj.getString("template");
        				String describeMap = obj.getString("describeMap");
        				String shopId = obj.getString("shopId");
        				String categoryPath = obj.getString("categoryPath");
        				ajaxRequestUrl.append("productId="+productId+
        									  "&templateType="+templateType+
        									  "&describeMap="+describeMap+
        									  "&shopId="+shopId+
        									  "&categoryPath="+categoryPath);
        				
        			}catch(JSONException je){
        				//在命令行打印异常信息在程序中出错的位置及原因
        				//Prints the exception information on the command line where and why the error occurred in the program
        				je.printStackTrace();
        			}	
        		}
        	}
        	
        	//get the AJAX information
        	try {
        		org.jsoup.Connection conn = Jsoup.connect(ajaxRequestUrl.toString());
        		conn.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
          		Document doc1 = conn.get();
        		
        		//get editor's choice, content, author's introduction and catalog
          		//translate the Unicode in the Web to string
           		String string = decodeUnicode(doc1.text());
        		JSONObject obj = new JSONObject(string);
           		String dataString = obj.getString("data");
        		JSONObject objData = new JSONObject(dataString);
        		String htmlString = objData.getString("html");
        		
        		//Use the format like </xxx> to segment characters
        		String[] datastrm = htmlString.split("[</a-z>\f\n\t\r ]+");
          		
        		
        		boolean hasCatalog=false;
        		boolean hasContent=false;
        		boolean hasEditorschoice=false;
        		boolean hasAuthorsintro=false;
        		
        		String cltext=null;
        		String ectext=null;
        		String cttext=null;
        		String aitext=null;
        		
        		for(String data: datastrm) {
        			//get catalog
        			if(hasCatalog) {
        				cltext=data;
        				hasCatalog=false;
        			}
        			
        			//get content
        			if(hasContent) {
        				cttext=data;
        				hasContent=false;
        			}
        			
        			//get editor's choice
        			if(hasEditorschoice) {
        				ectext=data;
        				hasEditorschoice=false;
        			}
        			
        			//get author's introduction
        			if(hasAuthorsintro) {
        				aitext=data;
        				hasAuthorsintro=false;
        			}
        			
        			if(data.contains("目　　录")) {
        				hasCatalog = true;
        			}
        			if(data.contains("内容简介")) {
        				hasContent = true;
        			}
        			if(data.contains("编辑推荐")) {
        				hasEditorschoice=true;
        			}
        			if(data.contains("作者简介")) {
        				hasAuthorsintro=true;
        			}
        			
        		}
        		
        		//if the information is missed
        		if(cltext==null) {
        			cltext="暂无";
        		}
        		if(ectext==null) {
        			ectext="暂无";
        		}
        		if(cttext==null) {
        			cttext="暂无";
        		}
        		if(aitext==null) {
        			aitext="暂无";
        		}
	
                book.setCatalog(cltext);
                book.setEditorsChoice(ectext);
                book.setContent(cttext);
                book.setAuthorIntroduction(aitext);
    		
            	//store the data to in JSON format
            	try {
					JsonWrite(book);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
        		
        	} catch(IOException e) {
        		e.printStackTrace();
        	} catch(JSONException je){
        		je.printStackTrace();
        	}
            
           	//test code used to show the book information 
//	    	System.out.println("[标题]"+book.getTitle());        	
//	    	System.out.println("[作者]"+book.getAuthor());//作者是
//	    	System.out.println("[所属分类]"+book.getType());//分类是
//	    	System.out.println("[出版社]"+book.getPress());//出版社是 
//	    	System.out.println("[图书照片] "+book.getPicInfo()); 
//	    	System.out.println("[价格] "+book.getCurrentPrice());
//	        System.out.println("[编辑推荐]"+book.getEditorsChoice());            		
//	    	System.out.println("[内容简介]"+book.getContent());
//	    	System.out.println("[作者简介]"+book.getAuthorIntroduction());
//	    	System.out.println("[目录]"+book.getCatalog());
//	    	System.out.println("[副标题]"+book.getHeadTitle()); 
//	    	System.out.println("[URL]"+book.getUrl());
        	
        }
    }
    
    /**
     * This function is to translate Unicode to string
     */
    public static String decodeUnicode(String unicode) {     
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicode);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            unicode = unicode.replace(matcher.group(1), ch+"" );
        }
        return unicode;
    }
    
    /**
     * This function is to output the information in JSON format
     */   
	public void JsonWrite(Book book) throws Exception{
		
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("book.json",true),"UTF-8");
		    JSONObject obj=new JSONObject();	//create a JSON object
	
	        obj.put("标题",book.getTitle());
		    obj.put("作者",book.getAuthor());
	        obj.put("分类",book.getType());
	        obj.put("出版社",book.getPress());
	        obj.put("价格","￥"+book.getCurrentPrice());
	        obj.put("图书照片",book.getPicInfo());
	        obj.put("编辑推荐",book.getEditorsChoice());
	        obj.put("内容简介",book.getContent());
	        obj.put("作者简介",book.getAuthorIntroduction());
	        obj.put("目录",book.getCatalog());  
	        obj.put("url",book.getUrl());
	        obj.put("副标题",book.getHeadTitle());
		        
		    System.out.println(obj.toString());
		    osw.write(obj.toString()+"\n");
		    osw.flush();						//Clear the buffer and force output data
		    osw.close();						//Close output stream
		}catch (IOException e) {
		   	 e.printStackTrace();
		    }
	}
}