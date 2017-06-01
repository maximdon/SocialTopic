package com.softlib.imatch.test.boost;
import org.junit.Test;
import static org.junit.Assert.*;


import com.softlib.imatch.matcher.boost.EqualsOperator;
import com.softlib.imatch.matcher.boost.GreaterThanOperator;

public class GreaterThanOperatorTest {

	@Test
	public void testLong() {
		GreaterThanOperator op = new GreaterThanOperator();
		assertTrue(op.isTrue("1001", "1000"));
	}

	@Test
	public void testFloat() {
		GreaterThanOperator op = new GreaterThanOperator();
		assertTrue(op.isTrue("1000.123", "1000.023"));
	}

	@Test
	public void testDate() {
		GreaterThanOperator op = new GreaterThanOperator();
		assertTrue(op.isTrue("23/05/1977", "24-05-1977"));
	}
}
