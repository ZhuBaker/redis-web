package com.arronlong.redisweb.common.redis.serial;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * spring-data-redis 序列化反序列化生成器
 * 
 * @author cuichenglong@motie.com  
 * @date 2017年11月24日 下午2:34:06
 */
@Setter
@Accessors(chain=true)
@SuppressWarnings("rawtypes")
public class RedisSerializerBuilder {

	private RedisTemplate redisTemplate;
	
	private RedisSerializer defaultSerializer = new JdkSerializationRedisSerializer();
//	private RedisSerializer defaultSerializer = new HessianSerializationRedisSerializer();
	private boolean enableDefaultSerializer = true;
	
	private RedisSerializer keySerializer = null;
	private RedisSerializer valueSerializer = null;
	private RedisSerializer hashKeySerializer = null;
	private RedisSerializer hashValueSerializer = null;
	private RedisSerializer<String> stringSerializer = new StringRedisSerializer();
	
	@SuppressWarnings({ "unchecked"})
	public RedisTemplate build() {
		redisTemplate.setEnableDefaultSerializer(enableDefaultSerializer);
		if(enableDefaultSerializer) {
			redisTemplate.setDefaultSerializer(defaultSerializer);
		}else {
			if(keySerializer!=null) {
				redisTemplate.setKeySerializer(keySerializer);
			}
			if(valueSerializer!=null) {
				redisTemplate.setValueSerializer(valueSerializer);
			}
			if(hashKeySerializer!=null) {
				redisTemplate.setHashKeySerializer(hashKeySerializer);
			}
			if(hashValueSerializer!=null) {
				redisTemplate.setHashValueSerializer(hashValueSerializer);
			}
			if(stringSerializer!=null) {
				redisTemplate.setStringSerializer(stringSerializer);
			}
		}
		return redisTemplate;
	}
}
