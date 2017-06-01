package com.softlib.imatch.matcher.boost;

public class ContainsOperator implements BinaryOperator
{
	public boolean isTrue(Object firstOperand, Object secondOperand) {
		if(firstOperand == null || secondOperand == null)
			return false;
		if(!(firstOperand instanceof String) || !(secondOperand instanceof String))
			return false;
		String firstOperandLower = ((String)firstOperand).toLowerCase();
		String secondOperandLower = ((String)secondOperand).toLowerCase();
		return firstOperandLower.contains(secondOperandLower);
	}
}
