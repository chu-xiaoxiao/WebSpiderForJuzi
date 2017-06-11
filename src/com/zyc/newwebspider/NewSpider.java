package com.zyc.newwebspider;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.Proxy.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Templates;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zyc.spider.JuZi;
import com.zyc.spider.JuziType;
import com.zyc.util.DBUtil;
import com.zyc.util.IpPool;
import com.zyc.util.MyException;


public class NewSpider {
	private static Integer count = 0;
	private static Set<String> newUrl = new HashSet<String>();
	private static IpPool ipPool = new IpPool("113.225.58.188");
	private static Set<JuZi> result = new HashSet<JuZi>();
	private static Set<JuZi> result1 = new HashSet<JuZi>();
	private static Set<JuziType> resultJuzitype = new HashSet<JuziType>();
	private static Set<String> reUrl = new HashSet<String>();
	private static Set<URL> page = new HashSet<URL>();
	private static Set<JuziType> types = new HashSet<JuziType>();
	
	public static void main(String[] args) {
		addUrlFromDbBiaoqian(null);
		for(JuziType juziType : types){
			try {
				addUrls(new URL(juziType.getPath()));
				for(URL Temp2 : page){
					Document document = getDocumet(Temp2);
					getJuZi(document);
					saveDBJuZi(juziType.getJuzitypeid());
					guanlian(juziType);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 标签添加的主函数备份
	 */
	public void addBiaoqianbak(){
		try {
			addUrls(new URL("http://www.juzimi.com/alltags"));
			for (String string : newUrl) {
				Document document = getDocumet(new URL(string));
				getBiaoQian(document);
				saveDBBiaoQian();
			}
			for (String string : reUrl) {
				Document document = getDocumet(new URL(string));
				getBiaoQian(document);
				saveDBBiaoQian();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到目标url的document文本
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Document getDocumet(URL url) throws IOException {
		System.out.println("连接到:" + url);
		boolean flag = true;
		Document document = null;
		Integer prcount = 0;
		while (flag) {
			try {
				NewSpider.count++;
				document = Jsoup.connect(url.toString()).proxy(ipPool.getIp(), ipPool.getPort())
						.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0")
						.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
						.get();
				flag = false;
				if (count > 1000) {
					count=0;
					throw new MyException("当前ip超过规定访问次数抛弃");
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				System.err.println("连接失败当前网址重试");
				ipPool.getNiMingIpAndPort();
				System.out.println("是匿名ip");
				System.out.println("当前使用ip:" + ipPool);
			}
		}
		return document;
	}

	/**
	 * 得到目标url所连接的跳转页（juzimi）
	 * 
	 * @param url
	 * @throws IOException
	 */
	public static void addUrls(URL url) throws IOException {
		Integer page1 = null;
		Document document = getDocumet(url);
		Elements elements =document.getElementsByClass("pager-last");
		if (elements.size() == 0) {
			elements = document.getElementsByClass("pager-item");
			if(elements.size()==0){
				page.add(new URL(url.toString()));
				return;
			}
			page1 = Integer.parseInt(elements.get(elements.size()-1).text());
			for (int i = 0; i < page1; i++) {
				if(i==0){
					page.add(new URL(url.toString()));
				}else{
					page.add(new URL(url.toString() + "?page=" + i));
				}
			}
			return;
		}
		page1= Integer.parseInt(elements.get(0).text());
		for (int i = 0; i < page1; i++) {
			if(i==0){
				page.add(new URL(url.toString()));
			}else{
				page.add(new URL(url.toString() + "?page=" + i));
			}
		}
	}
	/**
	 * 根据数据库中含有的标签生成链接
	 * @param url
	 */
	public static void addUrlFromDbBiaoqian(URL url){
		String sql = "select JUZITYPEID,JUZUTYPENAME,JUZITYPEPATH from juzitype";
		Connection connection = DBUtil.getCon();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				JuziType juziType = new JuziType(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
				types.add(juziType);
				System.out.println(juziType.getPath());
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将获取记录存至数据库
	 * 
	 * @throws SQLException
	 */
	public static void saveDBJuZi(Integer juzitypeid) throws SQLException {
		String sql = "insert into juzi (juziid,juzi,zuozhe) values(SEQ_JUZI.nextval,?,?)";
		Connection connection = DBUtil.getCon();
		int i = 0;
		for (JuZi temp : result) {
			try {
				PreparedStatement preparedStatement = null;
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, temp.getSentence());
				preparedStatement.setString(2, temp.getWriter());
				i = preparedStatement.executeUpdate();
				DBUtil.closePsRs(preparedStatement, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.clear();
		if (i > 0) {
			System.out.println("插入成功");
		} else {
			System.out.println("插入失败");
		}
		System.out.println("保存成功");
		DBUtil.closeCon();
	}
	public static void guanlian(JuziType juziType){
		String sql = "insert into juzitojizitype (TOTYPEID,JUZIID,JUZITYPEID) values(seq_juzitojizitype.nextval,?,?)";
		Connection connection = DBUtil.getCon();
		int i = 0;
		for (JuZi temp : result1) {
			try {
				PreparedStatement preparedStatement = null;
				String sql2 = "select * from juzi where juzi = ?";
				preparedStatement = connection.prepareStatement(sql2);
				preparedStatement.setString(1, temp.getSentence());
				ResultSet resultSet = preparedStatement.executeQuery();
				if(resultSet.next()){
					temp.setJuziid(resultSet.getInt(1));
				}
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, temp.getJuziid());
				preparedStatement.setInt(2, juziType.getJuzitypeid());
				i = preparedStatement.executeUpdate();
				DBUtil.closePsRs(preparedStatement, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result1.clear();
		if (i > 0) {
			System.out.println("插入成功");
		} else {
			System.out.println("插入失败");
		}
		System.out.println("保存成功");
		DBUtil.closeCon();
	}
	/**
	 * 将标签保存至数据库
	 * 
	 * @throws SQLException
	 */
	public static void saveDBBiaoQian() throws SQLException {
		String sql = "insert into juzitype (JUZITYPEID，JUZUTYPENAME,JUZITYPEPATH) values(seq_juzitype.nextval,?,?)";
		Connection connection = DBUtil.getCon();
		int i = 0;
		try {
			for (JuziType temp : resultJuzitype) {
				PreparedStatement preparedStatement = null;
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, temp.getJuzitypename());
				preparedStatement.setString(2, temp.getPath());
				i = preparedStatement.executeUpdate();
				DBUtil.closePsRs(preparedStatement, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultJuzitype.clear();
		if (i > 0) {
			System.out.println("插入成功");
		} else {
			System.out.println("插入失败");
		}
		System.out.println("保存成功");
		DBUtil.closeCon();
	}

	/**
	 * 解析一个句子网页所有的句子
	 * 
	 * @param document
	 */
	public static void getJuZi(Document document) {
		Elements elements = document.getElementsByAttributeValue("class", "views-field-phpcode");
		for (Element element : elements) {
			String juzi = element.child(0).text();
			String zuozhe = element.child(1).text();
			JuZi juZi2 = new JuZi(juzi, zuozhe);
			System.out.println(juZi2);
			result.add(juZi2);
			result1.add(juZi2);
		}
	}

	/**
	 * 解析一个标签页的所有标签
	 * 
	 * @param document
	 */
	public static void getBiaoQian(Document document) {
		Elements elements = document.getElementsByAttributeValue("class", "views-field-name");
		for (Element element : elements) {
			JuziType juziType = new JuziType(element.text(),"http://www.juzimi.com"+element.child(0).attr("href"));
			System.out.println(juziType);
			resultJuzitype.add(juziType);
		}
	}
}
