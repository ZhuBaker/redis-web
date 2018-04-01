package com.arronlong.redisweb.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

import com.arronlong.redisweb.common.exception.ConcurrentException;
import com.arronlong.redisweb.common.redis.MyStringRedisTemplate;

public abstract class RedisApplication implements Constant{

	private static Log log = LogFactory.getLog(RedisApplication.class);
	
	public static volatile RefreshModeEnum refreshMode = RefreshModeEnum.manually;
	public static volatile ShowTypeEnum showType = ShowTypeEnum.show;
	public static String BASE_PATH = "/redis-web";
	
	protected volatile Semaphore limitUpdate = new Semaphore(1);
	protected static final int LIMIT_TIME = 3; //unit : second
	
	public static ThreadLocal<Integer> redisConnectionDbIndex = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};
	
	protected static ThreadLocal<Semaphore> updatePermition = new ThreadLocal<Semaphore>() {
		@Override
		protected Semaphore initialValue() {
			return null;
		}
	};
	
	protected static ThreadLocal<Long> startTime = new ThreadLocal<Long>() {
		protected Long initialValue() {
			return 0l;
		}
	};
	
	private Semaphore getSempahore() {
		startTime.set(System.currentTimeMillis());
		updatePermition.set(limitUpdate);
		return updatePermition.get();
		
	}
	protected boolean getUpdatePermition() {
		Semaphore sempahore = getSempahore();
		boolean permit = sempahore.tryAcquire(1);
		return permit;
	}
	
	protected void finishUpdate() {
		Semaphore semaphore = updatePermition.get();
		if(semaphore==null) {
			throw new ConcurrentException("semaphore==null");
		}
		final Semaphore fsemaphore = semaphore;
		new Thread(new Runnable() {
			
			Semaphore RSemaphore;
			{
				RSemaphore = fsemaphore;
			}
			
			@Override
			public void run() {
				long start = startTime.get();
				long now = System.currentTimeMillis();
				try {
					long needWait = start + LIMIT_TIME * 1000 - now;
					if(needWait > 0L) {
						Thread.sleep(needWait);
					}
				} catch (InterruptedException e) {
					log.warn("finishUpdate 's release semaphore thread had be interrupted");
				}
				RSemaphore.release(1);
				logCurrentTime("semaphore.release(1) finish");
			}
		}).start();
	}
	
	//this idea is not good
	/*protected void runUpdateLimit() {
		new Thread(new Runnable () {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(LIMIT_TIME * 1000);
						limitUpdate = new Semaphore(1);
					} catch(InterruptedException e) {
						log.warn("runUpdateLimit 's new semaphore thread had be interrupted");
						break;
					}
				}
			}
		}).start();
	}*/
	
	/**
	 * 创建Redis链接（创建RedisTemplate、并缓存Redis中的key）
	 * 
	 * @param name
	 * @param host
	 * @param port
	 * @param password
	 */
	protected void createRedisConnection(String name, String host, int port, String password) {
		//创建RedisTemplate，并缓存key
		createRedisTemplate(name, host, port, password);
		
		//缓存Redis链接信息
		Map<String, Object> redisServerMap = new HashMap<String, Object>();
		redisServerMap.put("name", name);
		redisServerMap.put("host", host);
		redisServerMap.put("port", port);
		redisServerMap.put("password", password);
		RedisApplication.redisServerCache.add(redisServerMap);

		//初始化ztree
		RedisZtreeUtil.initRedisNavigateZtree(name);
	}
	
	/**
	 * 创建并缓存RedisTemplate，并缓存Redis中的key
	 * 
	 * @param name
	 * @param host
	 * @param port
	 * @param password
	 */
	public void createRedisTemplate(String name, String host, int port, String password) {
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
		connectionFactory.setHostName(host);
		connectionFactory.setPort(port);
		if(!StringUtils.isEmpty(password))
			connectionFactory.setPassword(password);
		connectionFactory.afterPropertiesSet();
		@SuppressWarnings("rawtypes")
		RedisTemplate redisTemplate = new MyStringRedisTemplate();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.afterPropertiesSet();
		RedisApplication.redisTemplatesMap.put(name, redisTemplate);
		
		//缓存key
		initRedisKeysCache(redisTemplate, name);
	}
	
	/**
	 * 初始化-缓存Redis中key
	 * 
	 * @param redisTemplate
	 * @param name
	 */
	@SuppressWarnings("rawtypes")
	private void initRedisKeysCache(RedisTemplate redisTemplate, String name) {
		for(int i=0;i<=REDIS_DEFAULT_DB_SIZE;i++) {
			initRedisKeysCache(redisTemplate, name, i);
		}
	}
	
	/**
	 * 初始化-缓存Redis中key
	 * 
	 * @param redisTemplate
	 * @param serverName
	 * @param dbIndex
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void initRedisKeysCache(RedisTemplate redisTemplate, String serverName , int dbIndex) {
		RedisConnection connection = RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory());
		connection.select(dbIndex);
		Set<byte[]> keysSet = connection.keys("*".getBytes());
		connection.close();
		List<RKey> tempList = new ArrayList<RKey>();
		ConvertUtil.convertByteToString(connection, keysSet, tempList);
		Collections.sort(tempList);
		CopyOnWriteArrayList<RKey> redisKeysList = new CopyOnWriteArrayList<RKey>(tempList);
		if(redisKeysList.size()>0) {
			redisKeysListMap.put(serverName+DEFAULT_SEPARATOR+dbIndex, redisKeysList);
		}
	}
	
	/**
	 * 初始化-缓存Redis的序列化器
	 * 
	 * @param key
	 * @param clazz
	 */
	protected void initRedisSerializerMap(String key, Class<RedisSerializer<?>> clazz) {
		redisSerializerMap.put(key, clazz);
	}
	
	
	protected static void logCurrentTime(String code) {
		log.debug("       code:"+code+"        当前时间:" + System.currentTimeMillis());
	}
}
