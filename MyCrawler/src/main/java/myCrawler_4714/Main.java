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

import java.util.Scanner;

/**
 * This class is the user interface, used for input and output.
 *
 * @version 	1.0  12 Jan 2021
 * @author 	    Siying Zhou
 */
public class Main {
/* After getting the keyword and the value, this class use LeceneProcess to get the corresponding book information and output. */
	
	/**
     * This function is to get input and use LeceneProcess to get the corresponding book information and output it.
     */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LuceneProcess w=new LuceneProcess();
		String filePath="data/index";//Create index storage directory
		Scanner s = new Scanner(System.in);
		System.out.println("欢迎使用当当图书搜索引擎！");
		
		while(true) {
			System.out.println("请输入0或1来表示是否开始");
			System.out.println("1：开始检索");
			System.out.println("0：退出");
			int start = s.nextInt();
			s.nextLine();
			if(start==0) {
				break;
			}
			if(start!=1) {
				System.out.println("无效输入");
				continue;
			}
			System.out.println("请输入关键字：");
			System.out.println("1：标题");
			System.out.println("2：作者");
			System.out.println("3：分类");
			System.out.println("4：出版社");
			
			int key = s.nextInt();
			s.nextLine();
			String index = null;

			switch(key) {
				case 1:
					index = "title";
					break;
				case 2:
					index = "author";
					break;
				case 3:
					index = "type";
					break;
				case 4:
					index = "press";
					break;
				default:
					System.out.println("无此关键字");
					continue;
			}
			System.out.println("请输入检索的内容");
			String queryStr = s.nextLine();
			if (!w.search(filePath, queryStr, index)) {
				System.out.println("无匹配记录");
			}
			
		}
		
		System.out.println("感谢您的使用！");
			
	}

}
