package com.zyc.spider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebSpider2 {
	private static Set<String> result = new HashSet<String>();
	private static Set<URL> urls = new HashSet<URL>();
	public static void main(String[] args) {
		try {
			
			Document document = getDocumet(new URL("http://www.juzimi.com/new"));
			Elements elements = document.getElementsByAttributeValue("class","views-field-phpcode");
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i <elements.size(); i++) {
				stringBuffer.append(elements.get(i).child(0).text());
				if(!elements.get(i).child(1).attr("class").equals("views-field-ops")){
					stringBuffer.append(elements.get(i).child(1).text());
				}else{
					stringBuffer.append("——无作者");
				}
				System.out.println(stringBuffer);
				result.add(stringBuffer.toString());
				stringBuffer.setLength(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				save();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Document getDocumet(URL url) throws IOException{
		System.out.println("连接到:"+url);
		Document document =Jsoup.connect(url.toString())
				.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
				.get();
		return document;
	}
	
	public static void save() throws FileNotFoundException{
		File file = new File(WebSpider2.class.getResource("result.txt").toString().substring(5));
		PrintWriter printWriter = new PrintWriter(new PrintWriter(file));
		for(String string : result){
			printWriter.println(string);
		}
		printWriter.close();
		System.out.println("记录保存至:"+file.getAbsolutePath());
	}
	
	public static void addUrls(URL url,Document document){
		Elements elements = document.getElementsByClass("pager-last");
		Integer page = Integer.parseInt(elements.get(0).text());
		for(int i=0;i<page;i++){
			try {
				urls.add(new URL(url.toString()+"?page=9"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
}
