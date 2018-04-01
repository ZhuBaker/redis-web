package com.arronlong.redisweb.common.webcontext;

import com.arronlong.redisweb.common.redis.RedisSerializerSetting;

import lombok.Getter;
import lombok.Setter;

/**
 * web运行期上下文
 * 
 * @author cuichenglong@motie.com  
 * @date 2017年11月24日 下午3:02:40
 */
public class WebRuntimeContext implements java.io.Serializable {

	private static final long serialVersionUID = -3093307430774602723L;

	@Getter @Setter
	private RedisSerializerSetting redisSerializerSetting;
}
