package com.softlib.imatch.test.common;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.softlib.imatch.common.SimpleCommaTokenizer;

public class SimpleCommaTokenizerTest {
	
	static private String TEXT = "aaa   bbb,ccc !!!1000 , ddd! 1,000.3 ,, 2,000 -3,000 eee&fff,ggg\nhhh\tiii\n\n\njjj;kkk"; 
	
	@Test
	public void test() {
		SimpleCommaTokenizer s = new SimpleCommaTokenizer(new char[] {' ','=','\n','\t','?','!',';','&'});
		List<String> splitList = new ArrayList<String>();
		
		int idx = 0;
		for (String str : s.split(TEXT)) {
			idx++;
			System.out.println("split["+idx+"]=\t"+str);
			splitList.add(str);
		}
		
		Assert.assertEquals("Should find 15 split word", 15 , splitList.size());
	}
	
};
