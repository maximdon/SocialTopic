package com.softlib.imatch.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.dictionary.TechnicalTermSource;

public class SourceMngr { 

	public static final String UserDefinedLocal = "User Defined Local";
	public static final Object UserDefined = "User Defined";
	
	public enum Type {
		Complex,
		Compound,
		Modified,
		LowFequency;
	};
	
	private static Map<Type,Set<String>> sourcesByType = new HashMap<Type,Set<String>>();
	
	static {
		Set<String> sourcesNames = new HashSet<String>();
		sourcesNames.add("Density");
		sourcesNames.add("Prox ");
		sourcesNames.add("Patterns");
		sourcesByType.put(Type.Complex,sourcesNames);
	}
	
	static {
		Set<String> sourcesNames = new HashSet<String>();
		sourcesNames.add("Density");
		sourcesNames.add("Prox ");
		sourcesByType.put(Type.Compound,sourcesNames);
	}

	static {
		Set<String> sourcesNames = new HashSet<String>();
		sourcesNames.add("Density");
		sourcesNames.add("Prox ");
		sourcesNames.add("PP ");
		sourcesNames.add("Patterns");
		sourcesByType.put(Type.Modified,sourcesNames);
	}

	static {
		Set<String> sourcesNames = new HashSet<String>();
		sourcesNames.add("errorCodes");
		sourcesNames.add("Versions");
		sourcesNames.add("variables");
		sourcesNames.add("quoteTerms");
		sourcesNames.add("dashedTerms");
		sourcesNames.add("exceptions");
		sourcesNames.add("Oracle Versions");
		sourcesNames.add("NLP Version Tokens");
		sourcesNames.add("File Names Path");
		sourcesByType.put(Type.LowFequency,sourcesNames);
	}

	static public boolean isSource(TechnicalTermSource source,Type type) {
		if (source==null)
			return false;
		return isSource(source.getsourceName(),type);
	}

	static public boolean isSource(String srcName,Type type) {
		Set<String> typeSourcesNames = sourcesByType.get(type);
		if (typeSourcesNames==null)
			return false;
		for (String sourceName : typeSourcesNames) {
			if (srcName.startsWith(sourceName))
				return true;
		}
		return false;
	}
	
};
