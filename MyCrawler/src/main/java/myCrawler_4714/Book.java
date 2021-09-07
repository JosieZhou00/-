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

/**
 * This class is used for the book information.
 *
 * @version 	1.0  12 Jan 2021
 * @author 	    Siying Zhou
 */
public class Book {
	private String _url;
	private String _title;
	private String _author;
	private String _type;
	private String _press;
	private String _picInfo;
	private String _currentPrice;
	private String _headTitle;
	private String _editorsChoice;
	private String _content;
	private String _authorIntroduction;
	private String _catalog;

	/**
     * This function is a default constructor.
     */
	public Book() {
		
	}
	
	/**
     * This function is a parameter constructor.
     */
	public Book(String url,String title,String author,String type,String press,String picInfo,String currentPrice,
				String headTitle,String editorsChoice,String content,String authorIntroduction,String catalog) {
		this._url = url;
		this._title = title;
		this._author = author;
		this._type = type;
		this._press = press;
		this._picInfo = picInfo;
		this._currentPrice = currentPrice;
		this._headTitle = headTitle;
		this._editorsChoice = editorsChoice;
		this._content = content;
		this._authorIntroduction = authorIntroduction;
		this._catalog = catalog;
		
	}
	
	public String getUrl(){
		return this._url;
	}
	public String getTitle(){
		return this._title;
	}
	public String getAuthor(){
		return this._author;
	}
	public String getType(){
		return this._type;
	}
	public String getPress(){
		return this._press;
	}
	public String getPicInfo() {
		return this._picInfo;
	}
	public String getCurrentPrice() {
		return this._currentPrice;
	}
	public String getHeadTitle(){
		return this._headTitle;
	}
	public String getEditorsChoice() {
		return this._editorsChoice;
	}
	public String getContent() {
		return this._content;
	}
	public String getAuthorIntroduction() {
		return this._authorIntroduction;
	}
	public String getCatalog() {
		return this._catalog;
	}
	


	public void setUrl(String url) {
		this._url=url;
	}
	public void setTitle(String title) {
		this._title = title;
	}
	public void setAuthor(String author) {
		this._author = author;		
	}	
	public void setType(String type) {
		this._type = type;		
	}
	public void setPress(String press) {
		this._press=press;
	}
	public void setPicInfo(String picInfo) {
		this._picInfo = picInfo;		
	}
	public void setCurrentPrice (String currentPrice) {
		this._currentPrice = currentPrice;
	}
	public void setHeadTitle (String headTitle) {
		this._headTitle = headTitle;
	}	
	public void setEditorsChoice (String editorsChoice) {
		this._editorsChoice = editorsChoice;
	}	
	public void setContent (String content) {
		this._content = content;
	}
	public void setAuthorIntroduction (String authorIntroduction) {
		this._authorIntroduction = authorIntroduction;
	}
	public void setCatalog (String catalog) {
		this._catalog = catalog;
	}
	
}
