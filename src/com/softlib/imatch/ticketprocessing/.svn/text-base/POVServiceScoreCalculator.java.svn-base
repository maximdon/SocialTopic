package com.softlib.imatch.ticketprocessing;

import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;

public class POVServiceScoreCalculator extends RangeTfIdfFormulaScoreCalculator
{

	public POVServiceScoreCalculator(IConfigurationResourceLoader loader) {
		super(loader);
	}

	@Override
	protected float dfMatrix(double percentage) {
		float result;
			 if (percentage <= 0.01) result = (float)1.00;
		else if (percentage <= 0.025) result = (float)0.90;
		else if (percentage <= 0.05) result = (float)0.75;
		else if (percentage <= 0.1 ) result = (float)0.50;
		else                          result = (float)0.20;
		return result;
	}
};
