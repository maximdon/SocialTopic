package com.softlib.imatch.matcher;

import java.text.DateFormat;
import java.text.ParseException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.BeanUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.matcher.boost.BinaryOperator;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("condition")
public class Condition {
	@XStreamAsAttribute
	private String fieldName;
	@XStreamAsAttribute
	@XStreamAlias("operator")
	private String operatorName;
	@XStreamAsAttribute
	private String value;
	@XStreamAsAttribute
	String text;
	@XStreamOmitField
	private Object realValue;
	@XStreamOmitField
	private BinaryOperator operator;

	private static Logger log = Logger.getLogger(Condition.class);

	public boolean isTrue(MatchCandidate candidate) {
		if (text != null) {
			return evaluateShellCondition(text, candidate);
		} else {
			initializeOperator();
			Object fieldValue = BeanUtils.getCandidateProperty(candidate, fieldName);			
			if (fieldValue == null)
				return false;
			return operator.isTrue(fieldValue, realValue);
		}
	}

	/*
	 * For condition like score > X returns the X to be used as a minimal value for this interval
	 */
	float getMinScore() { 
		if(fieldName.equals("score")) {
			if(realValue == null)
				initializeOperator();
			return (Float)realValue;
		}
		return -1;
	}
	
	private void initializeOperator() {
		synchronized (this) {
			if (operator == null) {
				operator = (BinaryOperator) RuntimeInfo.getCurrentInfo()
						.getBean(operatorName);
			}

			if (realValue == null) {
				realValue = parse(value);
			}
		}
	}

	private Object parse(String str) 
	{
		Object result;
		try {
			result = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			try {
				result = Float.parseFloat(str);
			} catch (NumberFormatException e1) {
				try {
					result = DateFormat.getInstance().parse(str);
				} catch (ParseException e3) {
					result = str;
				}
			}
		}
		return result;
	}
	
	private boolean evaluateShellCondition(String text, MatchCandidate candidate) {
		Interpreter bsh = new Interpreter();
		try {
			bsh.set("candidate", candidate);
			bsh.eval(text);
			Object tmpResult = bsh.get("result");
			if (tmpResult == null)
				throw new RuntimeException(
						"Condition result should be saved in a variable called 'result', like 'result = candidate.getScore() > 0.5'");
			LogUtils.debug(log, "Expression %s evaluated to %s", text,
					tmpResult.toString());
			return Boolean.parseBoolean(tmpResult.toString());
		} catch (EvalError e) {
			throw new RuntimeException("Invalidate java function specified,"
					+ text + " can't be evaluated as java function due to "
					+ e.getErrorText());
		}
	}
}
