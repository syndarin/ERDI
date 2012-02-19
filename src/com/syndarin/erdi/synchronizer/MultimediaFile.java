package com.syndarin.erdi.synchronizer;

public class MultimediaFile {
	
	private String name;
	private int type;
	private String md5;

	
	public MultimediaFile(String name, int type, String md5) {
		this.name = name;
		this.type = type;
		this.md5 = md5;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getMd5() {
		return md5;
	}


	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	
	
	
}
