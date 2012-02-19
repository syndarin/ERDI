package com.syndarin.erdi.entities;

public class SaluteModel {
	
	private int id;
	private int parentId;
	private String code;
	private String title;
	private String desription;
	private int shots;
	private int time;
	private float price;
	private String previewFile;
	private String pictureMD5;
	private String previewMD5;
	private String videoMD5;
	private String pictureFile;
	private String videoFile;
	private boolean favourite;
	
	// ===== CONSTRUCTORS =====


	// ===== GETTERS & SETTERS =====
	
	public String getPreviewFile() {
		return previewFile;
	}

	public void setPreviewFile(String previewFile) {
		this.previewFile = previewFile;
	}
	
	public String getPictureMD5() {
		return pictureMD5;
	}

	public void setPictureMD5(String pictureMD5) {
		this.pictureMD5 = pictureMD5;
	}

	public String getPreviewMD5() {
		return previewMD5;
	}

	public void setPreviewMD5(String previewMD5) {
		this.previewMD5 = previewMD5;
	}

	public String getVideoMD5() {
		return videoMD5;
	}

	public void setVideoMD5(String videoMD5) {
		this.videoMD5 = videoMD5;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesription() {
		return desription;
	}

	public void setDesription(String desription) {
		this.desription = desription;
	}

	public int getShots() {
		return shots;
	}

	public void setShots(int shots) {
		this.shots = shots;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getPictureFile() {
		return pictureFile;
	}

	public void setPictureFile(String pictureFile) {
		this.pictureFile = pictureFile;
	}

	public String getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(String videoFile) {
		this.videoFile = videoFile;
	}

	public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o!=null&&o instanceof SaluteModel){
			SaluteModel model=(SaluteModel)o;
			if(this.id==model.id){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	
	
	
}
