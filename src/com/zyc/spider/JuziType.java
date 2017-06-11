package com.zyc.spider;

public class JuziType {
	private Integer juzitypeid;
	private String juzitypename;
	private String path;
	/**
	 * 
	 */
	public JuziType() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param juzitypename
	 */
	public JuziType(String juzitypename) {
		super();
		this.juzitypename = juzitypename;
	}
	
	/**
	 * @param juzitypename
	 * @param path
	 */
	public JuziType(String juzitypename, String path) {
		super();
		this.juzitypename = juzitypename;
		this.path = path;
	}

	/**
	 * @param juzitypeid
	 * @param juzitypename
	 * @param path
	 */
	public JuziType(Integer juzitypeid, String juzitypename, String path) {
		super();
		this.juzitypeid = juzitypeid;
		this.juzitypename = juzitypename;
		this.path = path;
	}

	public Integer getJuzitypeid() {
		return juzitypeid;
	}

	public void setJuzitypeid(Integer juzitypeid) {
		this.juzitypeid = juzitypeid;
	}

	public String getJuzitypename() {
		return juzitypename;
	}
	public void setJuzitypename(String juzitypename) {
		this.juzitypename = juzitypename;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "JuziType [juzitypeid=" + juzitypeid + ", juzitypename=" + juzitypename + ", path=" + path + "]";
	}
	
}
