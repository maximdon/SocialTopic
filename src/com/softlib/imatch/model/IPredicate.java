package com.softlib.imatch.model;

public interface IPredicate<T> 
{
	boolean apply(T type, String test);
}
