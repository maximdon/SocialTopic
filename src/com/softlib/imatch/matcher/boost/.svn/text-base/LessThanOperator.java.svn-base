package com.softlib.imatch.matcher.boost;

import java.util.Date;

public class LessThanOperator implements BinaryOperator 
{	
	public boolean isTrue(Object firstOperand, Object secondOperand) {
		if(firstOperand instanceof Float && secondOperand instanceof Float)
			return (Float)firstOperand < (Float)secondOperand;
		if(firstOperand instanceof Long && secondOperand instanceof Long)
			return (Long)firstOperand < (Long)secondOperand;
		if(firstOperand instanceof String && secondOperand instanceof String)
			return ((String)firstOperand).compareTo((String)secondOperand) < 0;
		if(firstOperand instanceof Date && secondOperand instanceof Date)
			return ((Date)firstOperand).compareTo((Date)secondOperand) < 0;
		return false;		
	}
}
