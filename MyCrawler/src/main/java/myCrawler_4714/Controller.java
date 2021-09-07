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


import java.util.Random;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * This class is used to control the crawler.
 *
 * @version 	1.0  12 Jan 2021
 * @author 	    Siying Zhou
 */
public class Controller {
	public static void main(String[] args) throws Exception {
		
		//Define the crawler data storage location
        String crawkStirageFolder = "E:/CRAWLER/dangdang/test1";
        
        //Define 1 crawler, that is, 1 thread, the number of crawlers cannot exceed the thread of the cpu
        int numberOfCrawlers=1;
        
        //Define crawler configuration
        CrawlConfig config=new CrawlConfig();
        config.setCrawlStorageFolder(crawkStirageFolder);
        
        //set different user agent to prevent IP address blocking
        String[] ua = {
        		"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36 OPR/37.0.2178.32",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586",
                "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 BIDUBrowser/8.3 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.277.400 QQBrowser/9.4.7658.400",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 UBrowser/5.6.12150.8 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 TheWorld 7",
                "Mozilla/5.0 (Windows NT 6.1; W¡­) Gecko/20100101 Firefox/60.0",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.66"};
        Random random = new Random();
        
        config.setCrawlStorageFolder("data/tmp");
        config.setUserAgentString(ua[random.nextInt(15)]);
        
        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests).
        // Otherwise it may overload the target servers.
        config.setPolitenessDelay(3000);

        // You can set the maximum crawl depth here. The default value is -1 for unlimited depth.
        config.setMaxDepthOfCrawling(10);

        // You can set the maximum number of pages to crawl. The default value is -1 for unlimited number of pages.
        config.setMaxPagesToFetch(10000);

        // Should binary data should also be crawled? example: the contents of pdf, or the metadata of images etc
        config.setIncludeBinaryContentInCrawling(false);
        
        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);      

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed("http://book.dangdang.com/");
        controller.addSeed("http://book.dangdang.com/01.14.htm");
        controller.addSeed("http://book.dangdang.com/01.30.htm");
        controller.addSeed("http://book.dangdang.com/study");

        //Start the crawler
        controller.start(MyCrawler.class,numberOfCrawlers);

    }
}