package com.syndarin.erdi.entities;

public class SaluteCategory {
	
	private int id;
	private String title;
	private String description;
	
	public SaluteCategory(int id, String title, String description) {
		this.id = id;
		this.title = title;
		this.description = description;
	}

	public SaluteCategory() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o!=null&&o instanceof SaluteCategory){
			SaluteCategory category=(SaluteCategory)o;
			if(this.id==category.getId()){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	
}
