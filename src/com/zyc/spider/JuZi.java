package com.zyc.spider;

public class JuZi {
	private Integer juziid;
	private String writer ;
	private String sentence;
	/**
	 * 
	 */
	public JuZi() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param writer
	 * @param sentence
	 */
	public JuZi( String sentence,String writer) {
		super();
		this.writer = writer;
		this.sentence = sentence;
	}
	
	/**
	 * @param juziid
	 * @param writer
	 * @param sentence
	 */
	public JuZi(Integer juziid, String writer, String sentence) {
		super();
		this.juziid = juziid;
		this.writer = writer;
		this.sentence = sentence;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	
	public Integer getJuziid() {
		return juziid;
	}
	public void setJuziid(Integer juziid) {
		this.juziid = juziid;
	}
	@Override
	public String toString() {
		return "JuZi [juziid=" + juziid + ", writer=" + writer + ", sentence=" + sentence + "]";
	}
	
}
