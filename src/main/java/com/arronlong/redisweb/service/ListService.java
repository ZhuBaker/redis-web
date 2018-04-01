package com.arronlong.redisweb.service;

public interface ListService {

	public void updateListValue(String serverName, int dbIndex, String key, String value) ;

	public void delListValue(String serverName, int dbIndex, String key) ;

}
