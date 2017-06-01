package com.softlib.imatch.ticketprocessing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;

/**
 * This step allows term extraction based on a set of regular expressions defined in a special text file.
 * The file is pointed by getFileName().
 * The file format is OPERATION $$$ REPLACEMENT $$$ REGEX where $$$ is delimiter between fields
 * Regular expressions can run in single operation mode where each operation is self contained and doesn't influence next operations
 * or in multi operation mode where the result of previous operation is passed to next operation.
 * Multi operation mode is useful in scenarios like clean where set of regular expressions are responsible for text cleaning and should work in chain   
 * @author Maxim Donde
 *
 */
abstract class RegexFileStep extends BaseTicketProcessStep
{
	private static Logger log = Logger.getLogger(RegexFileStep.class);

	@Override
	public void run(String fieldName, ITicket ticket,
			ITechnicalTermsContainer termsContainer, StepContext context)
			throws MatcherException 
	{
		Scanner scanner = null;
		List<String> matches = new ArrayList<String>();
		String fieldData = getData(fieldName, ticket, context);
		if(StringUtils.isEmpty(fieldData))
			return;
		if(!isSingleOperationMode())
			matches.add(fieldData);
	    try {
	      scanner = new Scanner(new FileInputStream(RuntimeInfo.getCurrentInfo().getRealPath("/{RegexFolder}/" + getFileName())));
	      while (scanner.hasNextLine()){
	    	  String regexLine = scanner.nextLine();
	    	  if(regexLine.startsWith("<!--"))
	    		  continue;
	    	  //Each regex line should have the following format:
	    	  //Operation &&& Replacement &&& Regex
	    	  String[] regexLineParts = regexLine.split(" \\$\\$\\$ ");
	    	  RegexOperationType opType = RegexOperationType.valueOf(regexLineParts[0]);
	  		  RegexOperation regexOp = RegexOperation.getRegexOperation(opType, regexLineParts[2], regexLineParts[1]);
	  		  if(isSingleOperationMode())
	  			  matches.addAll(regexOp.run(fieldData));
	  		  else
	  			  matches = regexOp.run(matches); 
	      }
	      complete(ticket,fieldName, matches, termsContainer, context);
	    } catch (FileNotFoundException e) {
			throw new MatcherException("Unable to read regex file " + getFileName() + " reason " + e.getMessage());
		}
	    catch(Exception e) {
	    	LogUtils.error(log, "Unable to process regex %s due to %s", getFileName(), e.getMessage());
	    }
	    finally{
	      scanner.close();
	    }
	}
	
	protected void complete(ITicket ticket, String fieldName, List<String> matches, ITechnicalTermsContainer container, StepContext context) {
		container.startSession(fieldName,context.getTempTicketId(), getStepName());
		addFieldData(matches, context, container);
		LogUtils.debug(log, "Extract #%s# {%s-%s} Terms:%s", ticket.getId(), getStepName(), fieldName, matches);
		container.endSession((float)1.0, null, true);

		return;
	}

	protected void addFieldData(List<String> fieldData,StepContext context, ITechnicalTermsContainer container) {
		for(String termStr : fieldData) {
			if(RuntimeInfo.getCurrentInfo().isWebAppMode() || isEligable(termStr))
				addTerm(termStr, container);
		}
	}
	
	@Override
	public String getStepName() {
		String fileName = getFileName();
		return fileName.substring(0, fileName.indexOf('.'));
	}

	protected abstract String getFileName();
	
	protected abstract boolean isSingleOperationMode();
}
