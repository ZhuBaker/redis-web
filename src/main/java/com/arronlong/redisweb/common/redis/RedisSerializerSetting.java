package com.arronlong.redisweb.common.redis;

import org.springframework.data.redis.serializer.RedisSerializer;

import com.arronlong.redisweb.common.redis.serial.RedisSerializerBuilder;
import com.arronlong.redisweb.common.util.Constant;

import lombok.Data;

/**
 * redis序列化和反序列化设置
 * 
 * @author cuichenglong@motie.com
 * @date 2017年11月24日 下午3:05:39
 */
@Data
public class RedisSerializerSetting {

	/**
	 * 是否默认序列化实现类
	 */
	private boolean enableDefaultSerializer = true;
	/**
	 * 默认序列化实现类-全路径
	 */
	private String defaultSerializerKey = "String";

	/**
	 * key序列化实现类-全路径
	 */
	private String keySerializerKey;
	/**
	 * value序列化实现类-全路径
	 */
	private String valueSerializerKey;
	/**
	 * hash存储时，key的序列化实现类-全路径
	 */
	private String hashKeySerializerKey;
	/**
	 * hash存储时，value的序列化实现类-全路径
	 */
	private String hashValueSerializerKey;
	/**
	 * 字符串序列化实现类-全路径
	 */
	private String stringSerializerKey = "String";
	//
	// public void setEnableDefaultSerializer(boolean enableDefaultSerializer) {
	// if(enableDefaultSerializer) {
	//
	// }
	// }

	public RedisSerializerBuilder builder() {
		try {
			RedisSerializerBuilder builder = new RedisSerializerBuilder()
					.setEnableDefaultSerializer(isEnableDefaultSerializer())
					.setDefaultSerializer(Constant.redisSerializerMap.get(getDefaultSerializerKey()).newInstance());

			Class<RedisSerializer<?>> redisSerializerClass = Constant.redisSerializerMap.get(getKeySerializerKey());
			if (redisSerializerClass != null) {
				builder.setKeySerializer(redisSerializerClass.newInstance());
			}
			redisSerializerClass = Constant.redisSerializerMap.get(getValueSerializerKey());
			if (redisSerializerClass != null) {
				builder.setValueSerializer(redisSerializerClass.newInstance());
			}
			redisSerializerClass = Constant.redisSerializerMap.get(getHashKeySerializerKey());
			if (redisSerializerClass != null) {
				builder.setHashKeySerializer(redisSerializerClass.newInstance());
			}
			redisSerializerClass = Constant.redisSerializerMap.get(getHashValueSerializerKey());
			if (redisSerializerClass != null) {
				builder.setHashValueSerializer(redisSerializerClass.newInstance());
			}
			return builder;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /**
	// * 默认序列化实现类-全路径
	// */
	// private String defaultSerializerClass =
	// "com.mauersu.util.redis.serial.HessianSerializationRedisSerializer";
	//
	// /**
	// * key序列化实现类-全路径
	// */
	// private String keySerializerClass;
	// /**
	// * value序列化实现类-全路径
	// */
	// private String valueSerializerClass;
	// /**
	// * hash存储时，key的序列化实现类-全路径
	// */
	// private String hashKeySerializerClass;
	// /**
	// * hash存储时，value的序列化实现类-全路径
	// */
	// private String hashValueSerializerClass;
	// /**
	// * 字符串序列化实现类-全路径
	// */
	// private String stringSerializerClass =
	// "org.springframework.data.redis.serializer.StringRedisSerializer";

	// /**
	// * 生命周期：
	// * REQEUST：本次请求有效，请求结束自动清除
	// * SESSION：本次登录有效，退出后，自动清除
	// */
	// private String scope = "REQEUST";
}
