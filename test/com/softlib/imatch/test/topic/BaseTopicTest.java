package com.softlib.imatch.test.topic;
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
import com.softlib.imatch.test.topic.Sentiment;

public abstract class BaseTopicTest implements ITest
{
	@Test
	public void test()
	{
		//TODO
	}	

	protected abstract List<ITestCase> testCases();
	
	public List<ITicket> getTickets()
	{ 
		List<ITicket> result = new ArrayList<ITicket>();
		int idx = 0;
		for(ITestCase testCase : testCases())
		{
			result.add(createTicketFromCase(testCase, idx++));
		}
		return result;
	}

	private ITicket createTicketFromCase(ITestCase testCase, int idx) {
		InMemoryTicket ticket = new InMemoryTicket("cases", this.getClass().getSimpleName() + String.valueOf(idx), testCase.title(), testCase.body());
		return ticket;
	}
}
