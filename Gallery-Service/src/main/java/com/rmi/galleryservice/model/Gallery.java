package com.rmi.galleryservice.model;

import java.util.List;

public class Gallery {
	
	private int id;
	private List<Image> images;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	
	
	@Override
	public String toString() {
		return "Gallery [id=" + id + ", images=" + images + "]";
	}
	
	
}
