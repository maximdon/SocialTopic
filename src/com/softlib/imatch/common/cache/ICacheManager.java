package com.softlib.imatch.common.cache;

public interface ICacheManager<E> {

	E get(String objectId,String key);

	void put(String objectId,String key, E object);

	void delete(String objectId,String key);

	void deleteAll();

	void destroy();

}