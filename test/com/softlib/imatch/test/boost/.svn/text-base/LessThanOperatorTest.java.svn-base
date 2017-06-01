package com.softlib.imatch.test.boost;
import org.junit.Test;
import static org.junit.Assert.*;


import com.softlib.imatch.matcher.boost.EqualsOperator;
import com.softlib.imatch.matcher.boost.LessThanOperator;

public class LessThanOperatorTest {

	@Test
	public void testLong() {
		LessThanOperator op = new LessThanOperator();
		assertTrue(op.isTrue("1000", "1001"));
	}

	@Test
	public void testFloat() {
		LessThanOperator op = new LessThanOperator();
		assertTrue(op.isTrue("1000.123", "1000.223"));
	}

	@Test
	public void testDate() {
		LessThanOperator op = new LessThanOperator();
		assertTrue(op.isTrue("23/05/1977", "24-05-1977"));
	}
}
