package com.softlib.imatch.test.topic;

public interface ITestCase {
	public String title();
	public String body();
	public String topic();
	public Sentiment sentiment();
}
