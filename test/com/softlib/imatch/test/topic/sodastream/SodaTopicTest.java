package com.softlib.imatch.test.topic.sodastream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.connectors.test.ITest;
import com.softlib.imatch.connectors.test.TestTicketProvider;
import com.softlib.imatch.test.topic.BaseTopicTest;
import com.softlib.imatch.test.topic.ITestCase;
import com.softlib.imatch.test.topic.Sentiment;

public class SodaTopicTest extends BaseTopicTest
{
	
	@BeforeClass
	public static void init()
	{
	}
	
	@After
	public void clean()
	{		
	}

	@Override
	protected List<ITestCase> testCases() {
		ArrayList<ITestCase> result = new ArrayList<ITestCase>();
		result.add(new SodaTestCase1());
		return result;
	}
	
}
