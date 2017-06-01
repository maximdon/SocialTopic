package com.softlib.imatch.test.boost;
import org.junit.Test;
import static org.junit.Assert.*;


import com.softlib.imatch.matcher.boost.ContainsOperator;
import com.softlib.imatch.matcher.boost.EqualsOperator;

public class ContainsOperatorTest {

	@Test
	public void testString() {
		ContainsOperator op = new ContainsOperator();
		assertTrue(op.isTrue("String 1234567", "strinG 12345"));
	}
}
