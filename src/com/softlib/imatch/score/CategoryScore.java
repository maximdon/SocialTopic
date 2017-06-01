package com.softlib.imatch.score;

public class CategoryScore {
	private String title;
	private float score;
	private String extra;
	
	public CategoryScore(String title, float score, String extra) {
		super();
		this.title = title;
		this.score = score;
		this.extra = extra;
	}
	
	public CategoryScore() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
	
	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getExtra() {
		return extra;
	}

	@Override
	public String toString() {
		return "category: " + this.title + " score: " + this.score  + " extra: " + this.extra + "\n";
	}
}