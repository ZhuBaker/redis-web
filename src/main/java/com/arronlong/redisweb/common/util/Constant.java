package com.arronlong.redisweb.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.arronlong.redisweb.common.ztree.ZNode;

@SuppressWarnings("rawtypes")
public interface Constant {
	
	/** 用于存储redistemplate */
	public static final Map<String, RedisTemplate> 					redisTemplatesMap 		= 	new HashMap<String, RedisTemplate>();
	/** 用于缓存redis中的key */
	public static final Map<String, CopyOnWriteArrayList<RKey>> 	redisKeysListMap 		= 	new HashMap<String, CopyOnWriteArrayList<RKey>>();
	public static final Map<RKey, Object> 							redisVMCache 			= 	new ConcurrentHashMap<RKey, Object>();
	/** 用于缓存ztree节点信息 */
	public static final CopyOnWriteArrayList<ZNode> 				redisNavigateZtree 		= 	new CopyOnWriteArrayList<ZNode>();
	/** 用于缓存redis服务器信息 */
	public static final CopyOnWriteArrayList<Map<String, Object>>	redisServerCache 		=   new CopyOnWriteArrayList<Map<String, Object>>();
	/** 用于缓存redis的序列化器 */
	public static final Map<String, Class<RedisSerializer<?>>>		redisSerializerMap 		=   new HashMap<>();

	public static final int DEFAULT_ITEMS_PER_PAGE											= 	10;
	public static final String DEFAULT_REDISKEY_SEPARATOR		 							= 	":";
	public static final int REDIS_DEFAULT_DB_SIZE 											= 	15;
	public static final String DEFAULT_SEPARATOR											= 	"_";
	public static final String UTF_8 														= 	"utf-8";
	
	/** redis properties key **/
	public static final String REDIS_SERVER_NUM_KEY											=	"redis.server.num";
	public static final String REDIS_LANGUAGE_KEY											= 	"redis.language";
	
	public static final String REDIS_HOST_PROFIXKEY											= 	"redis.host.";
	public static final String REDIS_NAME_PROFIXKEY											= 	"redis.name.";
	public static final String REDIS_PORT_PROFIXKEY											= 	"redis.port.";
	public static final String REDIS_PASSWORD_PROFIXKEY										= 	"redis.password.";
	
	/** serializer properties key **/
	public static final String SERIALIZER_LIST_NUM_KEY									= 	"serializer.list.num";
	
	public static final String SERIALIZER_KEY_PROFIXKEY									= 	"serializer.key.";
	public static final String SERIALIZER_CLASS_PROFIXKEY									= 	"serializer.class.";
	
	
	/** default **/
	public static final String DEFAULT_REDISSERVERNAME 										= "default";
	public static final int DEFAULT_DBINDEX 												= 0;
	
	/** query key **/
	public static final String MIDDLE_KEY													= "middle";
	public static final String HEAD_KEY 													= "head";
	public static final String TAIL_KEY 													= "tail";
	public static final String EMPTY_STRING 												= "";
	
	/** operator for log **/
	public static final String GETKV 														= "GETKV";
}
