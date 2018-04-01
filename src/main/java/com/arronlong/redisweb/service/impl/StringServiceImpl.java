package com.arronlong.redisweb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arronlong.redisweb.dao.RedisDao;
import com.arronlong.redisweb.service.HashService;
import com.arronlong.redisweb.service.StringService;

@Service
public class StringServiceImpl implements StringService {

	@Autowired
	private RedisDao redisDao;
	
	@Override
	public void delValue(String serverName, int dbIndex, String key) {
		redisDao.delRedisValue(serverName, dbIndex, key);
	}

	@Override
	public void updateValue(String serverName, int dbIndex, String key, String value) {
		redisDao.updateValue(serverName, dbIndex, key, value);
	}
	
}
