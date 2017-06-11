package com.zyc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class IpPool {
	private static List<String> urls = new ArrayList<String>();
	private static String ip;
	private static String port;
	private static String ipAndPort;
	private static String localIp;
	
	/**
	 * 
	 */
	public IpPool(String localip) {
		super();
		try {
			newUrls();
		} catch (IOException e) {
			e.printStackTrace();
		}
		localIp = localip;
		IpPool.readUrls();
	}
	
	public static void newUrls() throws IOException {
		System.out.println("Get new IP from api to ip.txt");
		Document document = Jsoup.connect("http://api.xicidaili.com/free2016.txt").get();
		String[] strings = document.body().text().split(" ");
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(IpPool.class.getResource("ip.txt").toString().substring(5)))));
		for (String temp : strings) {
			bufferedWriter.write(temp);
			bufferedWriter.newLine();
			urls.add(temp);
		}
		bufferedWriter.close();
		IpPool.readUrls();
	}
	/**
	 * 	niming1
	 * @throws IOException
	 */
	public static void newUrlsnimimg() throws IOException {
		System.out.println("Get new IP from api to ip.txt");
		Document document = Jsoup.connect("http://www.66ip.cn/nmtq.php?getnum=300&isp=0&anonymoustype=3&start=&ports=&export=&ipaddress=&area=0&proxytype=2&api=66ip").header("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36").get();
		String[] ips = document.body().text().split(" ");
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(IpPool.class.getResource("ip.txt").toString().substring(5)))));
		for (String temp : ips) {
			bufferedWriter.write(temp);
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
		//IpPool.readUrls();
	}

	public String getNewIpAndPort() throws IOException, MyException {
		if (urls.size() <= 0) {
			System.out.println("当前无可用ip");
			System.out.println("重新获得");
			IpPool.newUrls();
		}
		System.out.println("Ip get from ippool");
		ipAndPort = urls.remove(0);
		String[] strings = ipAndPort.split(":");
		ip = strings[0];
		port = strings[1];
		if(Integer.parseInt(port)>=65534){
			port = "8080";
		}else{
			port = strings[1];
		}
		return ipAndPort;
	}
		
	public String getNiMingIpAndPort(){
		try {
			this.getNewIpAndPort();
		while(!IpPool.isNiMing(localIp)){
			System.out.println(ipAndPort+" 不是匿名ip抛弃"+urls.size());
			try {
				this.getNewIpAndPort();
			} catch (IOException | MyException e) {
				e.printStackTrace();
			}
		}
		System.out.println("池内ip为"+ipAndPort);
		System.out.println("匿名ip获取成功");
		} catch (IOException | MyException e1) {
			e1.printStackTrace();
		}
		return ipAndPort;
	}

	public String getIpAndPort() {
		String strings = ip + ":" + port;
		return strings;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return Integer.parseInt(port);
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "IpPool [ip=" + ip + ", port=" + port + ", ipAndPort=" + ipAndPort + "]";
	}

	public static void getNewFromHttp() {
		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
		try {
			URL url = new URL("http://www.kuaidaili.com/free/inha/1/");
			Document document = Jsoup.connect(url.toString())
					.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0")
					.get();
			Elements elements = document.getElementsByAttributeValue("data-title", "IP");
			Elements elements2 = document.getElementsByAttributeValue("data-title", "PORT");
			for (Integer i = 0; i < elements.size(); i++) {
				String string = elements.get(1).text() + ":" + elements2.get(i).text();
				urls.add(string);
			}
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(IpPool.class.getResource("ip.txt").toString().substring(5)))));
			for (String temp : urls) {
				bufferedWriter.write(temp);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readUrls() {
		try {
			String temp;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(IpPool.class.getResource("ip.txt").toString().substring(5)))));
			while ((temp = bufferedReader.readLine()) != null) {
				urls.add(temp);
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isNiMing(String ip){
		Document document = null;
		try {
			document = Jsoup.connect("http://123.206.8.180/1")
					.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0")
					.proxy(IpPool.ip,Integer.parseInt(IpPool.port))
					.get();
			Elements elements = document.getElementsByTag("center");
			String string = elements.text();
				/*	String[] strings = string.split("\\*");
			if(strings.length<1){
				return false;
			}*/
			if(string.contains(ip)){
				return false;
			}else{
				System.out.println(string);
				return true;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
}
