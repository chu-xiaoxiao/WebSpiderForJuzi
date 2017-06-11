package com.zyc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;


public class DBUtil {
	private static DataSource ds;
	private static ThreadLocal<Connection> con = new ThreadLocal<Connection>();
	static {
		Properties p=new Properties();
		InputStream is=DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
		try {
			p.load(is);
			DBUtil.ds=BasicDataSourceFactory.createDataSource(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	static{
		try {
			Context context=new InitialContext();
			ds=(DataSource) context.lookup("java:comp/env/jdbc/umadmin");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}*/
	public static Connection getCon() {
		try {
			if (DBUtil.con.get() == null||DBUtil.con.get().isClosed()) {
				con.set(ds.getConnection());
				return con.get();
		}else{
			return con.get();
		}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		return con.get();

	}
	/*public static Connection getCon() {
		try {
			con = ds.getConnection();
			BasicDataSource basicDataSource = new BasicDataSource();
			System.out.println("当前使用连接"+basicDataSource.getNumActive());
			return con;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	public static void closeCon() {
		System.out.println("释放链接");
		try {
			DBUtil.con.get().close();
			DBUtil.con.remove();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
/*	public static void closeCon() {
		try {
			con.close();
			System.out.println("释放连接");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	public static void closePsRs(PreparedStatement ps, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 启动事物
	public static void beginTrans(Connection con) {
		try {
			
			con.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 提交
	public static void commitTrans(Connection con) {
		try {
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 回滚
	public static void rollbackTrans(Connection con) {
		try {
			con.rollback();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Object change(String string){
		Object temp = string;
		if(string!=null && !"".equals(string)){
			try{
				temp=Integer.parseInt(string.trim());
				return temp;
			}catch(Exception exception){
				try{
					temp = Double.parseDouble(string.trim());
					return temp;
				}catch (Exception exception1) {
					temp = string.trim();
					return temp;
				}
			}
		}else{
			return null;
		}
	}

}
