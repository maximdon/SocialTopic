package com.softlib.imatch.common.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.softlib.imatch.RuntimeInfo;

public class EHCacheManager<E> implements ICacheManager<E> {
	private CacheManager cacheMgr;
	
	private static final String CACHE_NAME = "ticketsCache";

	private final String cacheName;
	
	public EHCacheManager() {
		this(CACHE_NAME);
	}

	public EHCacheManager(String cacheName) {
		this.cacheName = cacheName;
		
		String cacheConfigFilePath = RuntimeInfo.getCurrentInfo().getRealPath(
				"/{SolutionConfigFolder}/cacheConfig.xml");
		cacheMgr = new CacheManager(cacheConfigFilePath);
	}

	private String getKey(String objectId,String key) {
		return objectId + "__" + key;
	}
	
	public void shutdown() {
		cacheMgr.shutdown();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.softlib.imatch.dbintegration.ICacheManager#get(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public E get(String objectId,String key) {
		E cachedObject = null;
		Cache cache = cacheMgr.getCache(cacheName);
		Element element = cache.get(getKey(objectId,key));
		if(element != null)
			cachedObject = (E) element.getObjectValue();
		return cachedObject; 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.softlib.imatch.dbintegration.ICacheManager#put(java.lang.String,
	 * java.lang.Object)
	 */
	public void put(String objectId,String key, E object) {
		Cache cache = cacheMgr.getCache(cacheName);
		Element element = new Element(getKey(objectId,key), object);
		cache.put(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.softlib.imatch.dbintegration.ICacheManager#delete(java.lang.String)
	 */
	public void delete(String objectId,String key) {
		Cache cache = cacheMgr.getCache(cacheName);
		cache.remove(getKey(objectId,key));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.softlib.imatch.dbintegration.ICacheManager#deleteAll()
	 */
	public void deleteAll() {
		Cache cache = cacheMgr.getCache(cacheName);
		cache.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.softlib.imatch.dbintegration.ICacheManager#destroy()
	 */
	public void destroy() {
		cacheMgr.shutdown();
	}
}
