package com.softlib.imatch.dictionary;

/**
 * Special text handler is responsible for handling special text like java exceptions, xml code, SQL query and etc
 * @author Maxim Donde
 *
 */
public interface ISpecialTextHandler 
{
	/**
	 * Checks if the given text is a special one (according to definition of this handler) 
	 * @param text
	 * @return
	 */
	public boolean match(String text);	
	
	/**
	 * Removes all special text (according to definition of this handler) from the given string and returns new string without special text 
	 * @param text
	 * @return
	 */
	public String remove(String text);

	/**
	 * If the given text is special text (according to definition of this handler) i.e. match(text) returns true
	 * This function is responsible for transforming given text to the canonical form (according to definition of this handler)   
	 * @param text
	 * @return
	 */
	public String toCanonicalForm(String text);
}
