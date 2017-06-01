package com.softlib.imatch.matcher.boost;


public class EqualsOperator implements BinaryOperator 
{
	public boolean isTrue(Object firstOperand, Object secondOperand) {
		if(firstOperand == null)
			return secondOperand  == null;
		return firstOperand.equals(secondOperand);
	}
}
