package com.softlib.imatch.test.boost;
import org.junit.Test;
import static org.junit.Assert.*;


import com.softlib.imatch.matcher.boost.EqualsOperator;

public class EqualsOperatorTest {

	@Test
	public void testLong() {
		EqualsOperator op = new EqualsOperator();
		assertTrue(op.isTrue("1000", "1000"));
	}

	@Test
	public void testFloat() {
		EqualsOperator op = new EqualsOperator();
		assertTrue(op.isTrue("1000.123", "1000.123"));
	}

	@Test
	public void testString() {
		EqualsOperator op = new EqualsOperator();
		assertTrue(op.isTrue("String 123", "strinG 123"));
	}

	@Test
	public void testDate() {
		EqualsOperator op = new EqualsOperator();
		assertTrue(op.isTrue("23/05/1977", "23-05-1977"));
	}
}
