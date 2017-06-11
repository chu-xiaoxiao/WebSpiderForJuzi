package com.zyc.spider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.zyc.util.DBUtil;
import com.zyc.util.IpPool;
import com.zyc.util.MyException;

public class WebSpider {
	private static Set<JuZi> result = new HashSet<JuZi>();
	private static Set<URL> urls = new HashSet<URL>();
	private static Set<URL> filadeUrls = new HashSet<URL>();

	private static Stack<URL> allUrls = new Stack<URL>();
	private static Set<URL> oldUrls = new HashSet<URL>();
	
	private static IpPool ipPool = new IpPool("113.225.58.188");
	private static int count = 0;

	private static Integer counet = 0;
	public static void main(String[] args) throws URISyntaxException, IOException {
		try {
			ipPool.getNiMingIpAndPort();
			getToEndPage("http://www.juzimi.com/todayhot");
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static Document getDocumet(URL url) throws IOException {
		System.out.println("连接到:" + url);
		boolean flag = true;
		Document document = null;
		while(flag){
			try {
				WebSpider.counet++;
				document = Jsoup.connect(url.toString())
						.proxy(ipPool.getIp(), ipPool.getPort())
						.header("User-Agent",
								"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
						.get();
				flag = false;
				if(counet>50){
					throw new MyException("当前ip超过访问次数抛弃");
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				System.err.println("连接失败当前网址重试");
				ipPool.getNiMingIpAndPort();
				System.out.println("是匿名ip");
				System.out.println("当前使用ip:"+ipPool);
			}
		}
		return document;
	}

	public static void save() throws FileNotFoundException {
		File file = new File(WebSpider.class.getResource("result.txt").toString().substring(5));
		PrintWriter printWriter = new PrintWriter(new PrintWriter(file));
		for (JuZi string : result) {
			printWriter.println(string.getSentence()+string.getWriter());
		}
		printWriter.close();
		System.out.println("记录保存至:" + file.getAbsolutePath());
	}

	public static void addUrls(URL url) throws IOException {
		Elements elements = getDocumet(url).getElementsByClass("pager-last");
		if (elements.size() == 0) {
			return;
		}
		Integer page = Integer.parseInt(elements.get(0).text());
		for (int i = 0; i < page; i++) {
			urls.add(new URL(url.toString() + "?page=" + i));
		}
	}

	public static void getToEndPage(String url) throws SQLException {
		URL jilu = null;
		try {
			addUrls(new URL(url));
			for (URL temp : urls) {
				try {
					jilu = temp;
					Document document = getDocumet(temp);
					Elements elements = document.getElementsByAttributeValue("class", "views-field-phpcode");
					StringBuffer stringBuffer = new StringBuffer();
					for (int i = 0; i < elements.size(); i++) {
						JuZi juZi = new JuZi();
						juZi.setSentence(elements.get(i).child(0).text());
						if (!elements.get(i).child(1).attr("class").equals("views-field-ops")) {
							juZi.setWriter(elements.get(i).child(1).text());
						} else {
							juZi.setWriter("——无作者");
						}
						System.out.println(juZi);
						result.add(juZi);
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("获取失败" + url + "重新加载");
					filadeUrls.add(temp);
					saveDB();
				}
				saveDB();
			}
			if (filadeUrls.size() != 0) {
				System.out.println("失败url重试");
				for (URL temp : filadeUrls) {
					try {
						JuZi juZi = new JuZi();
						jilu = temp;
						Document document = getDocumet(temp);
						Elements elements = document.getElementsByAttributeValue("class", "views-field-phpcode");
						StringBuffer stringBuffer = new StringBuffer();
						for (int i = 0; i < elements.size(); i++) {
							juZi.setSentence(elements.get(i).child(0).text());
							if (!elements.get(i).child(1).attr("class").equals("views-field-ops")) {
								juZi.setWriter(elements.get(i).child(1).text());
							} else {
								juZi.setWriter("——无作者");
							}
							System.out.println(stringBuffer);
							result.add(juZi);
							System.out.println(juZi);
						}
					} catch (IOException e) {
						e.printStackTrace();
						System.err.println("获取失败" + url);
					}
					saveDB();
				}
			}
			saveDB();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				save();
				saveDB();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static void initUrl(String urlTemp) throws IOException {
		URL url = new URL(urlTemp);
		System.out.println("扫描超链接.....:"+url);
		Document document = Jsoup.connect(url.toString())
				.header("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.header("Referer", "https://www.baidu.com/link?url=FmUby659K5uqr1xUm_z4tZl7-Eq95sz9hj3D2uU6PMO&wd=&eqid=9cd94337000235140000000458f9b1ca")
				.header("Proxy-Authorization", "authHeader")
				.get();
		Elements elements = document.getElementsByTag("a");
		for (int i = 0; i < elements.size(); i++) {
			System.out.println(elements.get(i).attr("href"));
			String url1 = elements.get(i).attr("href");
			if (url1.startsWith("/")) {
				url = new URL(url.toString() + url1);
			}
			System.out.println(url);
			allUrls.add(url);
		}
	}
	public static void saveDB() throws SQLException{
		count++;
		String sql = "insert into juzi (juziid,juzi,zuozhe) values(SEQ_JUZI.nextval,?,?)";
		Connection connection = DBUtil.getCon();
		int i = 0;
		try {
			for(JuZi temp : result){
				PreparedStatement preparedStatement = null;
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, temp.getSentence());
				preparedStatement.setString(2, temp.getWriter());
				i = preparedStatement.executeUpdate();
				DBUtil.closePsRs(preparedStatement,null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.clear();
		if(i>0){
			System.out.println("插入成功");
		}else{
			System.out.println("插入失败");
		}
		System.out.println("保存成功");
		System.out.println(count);
		DBUtil.closeCon();
	}
	public static Proxy daiLi(String proxyTypeUrl) throws URISyntaxException{
        // Must first set useSystemProxies to true. if not,cann't detect proxy.

        System.setProperty("java.net.useSystemProxies", "true");

        List<Proxy>proxyList = null;

        proxyList =ProxySelector.getDefault().select(new URI(proxyTypeUrl));

        for (int j = 0; j <proxyList.size(); j++) {

            Proxy proxy =proxyList.get(j);

            if (proxy.type() !=Proxy.Type.DIRECT) {

                return proxy;

            }

        }
        return null;
	}
}
