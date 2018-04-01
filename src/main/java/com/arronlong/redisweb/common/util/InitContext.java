package com.arronlong.redisweb.common.util;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import com.arronlong.redisweb.common.exception.RedisInitException;

@Component
public class InitContext extends RedisApplication implements Constant  {

	private static Log log = LogFactory.getLog(InitContext.class);
	
	@Autowired
	private Environment env;
	
	@PostConstruct
	public void initRedisServers() {
		String currentServerName = "";
		try {
			int serverNum = Integer.parseInt(env.getProperty(REDIS_SERVER_NUM_KEY));
			for(int i=1;i<=serverNum;i++) {
				//在初始化过程中出现问题，则跳过
				try {
					String name = env.getProperty(REDIS_NAME_PROFIXKEY + i);
					String host = env.getProperty(REDIS_HOST_PROFIXKEY + i);
					int port = Integer.parseInt(env.getProperty(REDIS_PORT_PROFIXKEY + i));
					String password = env.getProperty(REDIS_PASSWORD_PROFIXKEY + i);
					currentServerName = host;
					createRedisConnection(name, host, port, password);
				} catch (Throwable e1) {
					log.error("initRedisServers: " + currentServerName+" occur Throwable :" + e1.getMessage());
//					throw new RedisInitException(currentServerName + " init failed", e1);
				}
			}
		} catch (NumberFormatException e) {
			log.error("initRedisServers: " + currentServerName+" occur NumberFormatException :" + e.getMessage());
			throw new RedisInitException(e);
		}
	}
	
	@SuppressWarnings({ "unchecked"})
	@PostConstruct
	public void initRedisSerializers() {
		String clazz = null;
		try {
			int serializerNum = Integer.parseInt(env.getProperty(SERIALIZER_LIST_NUM_KEY));
			for(int i=1;i<=serializerNum;i++) {
				//初始化失败，则直接跳过
				try {
					String key = env.getProperty(SERIALIZER_KEY_PROFIXKEY + i);
					clazz = env.getProperty(SERIALIZER_CLASS_PROFIXKEY + i);
					
					initRedisSerializerMap(key, (Class<RedisSerializer<?>>)Class.forName(clazz));
				} catch (Throwable e1) {
					log.error("initRedisSerializers: " + clazz+" occur Throwable :" + e1.getMessage());
					//throw new RedisInitException(clazz + " init failed", e1);
				}
			}
		} catch (NumberFormatException e) {
			log.error("initRedisSerializers: " + clazz+" occur NumberFormatException :" + e.getMessage());
			throw new RedisInitException(e);
		}
	}
	
	/**
	 * 重载配置文件中配置的Redis
	 */
	public void reloadRedisServers() {
		//清除RedisTemplate缓存
		Constant.redisTemplatesMap.clear();
		Constant.redisServerCache.clear();
		Constant.redisKeysListMap.clear();
		Constant.redisVMCache.clear();
		Constant.redisNavigateZtree.clear();
		Constant.redisServerCache.clear();
		
		initRedisServers();
	}
	
	/**
	 * 初始化redis链接，包括手动添加的
	 */
	public void reInitRedisServers() {
		//清除RedisTemplate缓存
		Constant.redisTemplatesMap.clear();
		
		String currentServerName = "";
		for(Map<String, Object> map: Constant.redisServerCache) {
			//在初始化过程中出现问题，则跳过
			try {
				String name = (String) map.get("name");
				String host = (String) map.get("host");
				int port = (Integer) map.get("port");
				String password = (String) map.get("password");
				currentServerName = host;
				
				createRedisTemplate(name, host, port, password);
			} catch (Throwable e) {
				log.error("reInitRedisServers: " + currentServerName+" occur Throwable :" + e.getMessage());
			}
		}
	}
	
	
}
