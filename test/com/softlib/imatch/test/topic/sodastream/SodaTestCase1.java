package com.softlib.imatch.test.topic.sodastream;
import com.softlib.imatch.test.topic.ITestCase;
import com.softlib.imatch.test.topic.Sentiment;

public class SodaTestCase1 implements ITestCase
{	
	public String title()
	{
		return "SodaStream plans to start selling a machine that carbonates alcoholic drinks next year.";
	}
	
	public String body()
	{
		return "Here’s what I learned from drinking carbonated vodka";
	}
	
	public String topic()
	{
		return "carbonate alcoholic drinks";
	}
	
	public Sentiment sentiment()
	{
		return Sentiment.Neutral_Sentiment;
	}
}
