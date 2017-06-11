package com.zyc.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.nodes.Document;

public class ProxyGetDocument {
	private String ip;
	private Integer port;
	
	public ProxyGetDocument(String ip, Integer port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public Document getDocument(String url) throws MalformedURLException, IOException{
		InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
		Proxy proxy = new Proxy(Proxy.Type.HTTP,inetSocketAddress);
		URLConnection urlConnection = new URL(url).openConnection(proxy);
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String temp;
		while((temp = bufferedReader.readLine())!=null){
			System.out.println(temp);
		}
		return null;
	}
}
