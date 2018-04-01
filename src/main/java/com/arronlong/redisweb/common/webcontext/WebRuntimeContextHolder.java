package com.arronlong.redisweb.common.webcontext;

/**
 * web运行期上下文
 * 
 * @author cuichenglong@motie.com  
 * @date 2017年11月24日 下午3:03:01
 */
public class WebRuntimeContextHolder {

	private static ThreadLocal<WebRuntimeContext> context = new ThreadLocal<>();
	
	public static WebRuntimeContext getRuntimeContext(){
		if(context.get() != null){
			return context.get();
		} else {
			WebRuntimeContext pr = new WebRuntimeContext();
			context.set(pr);
			return pr;
		}
	}	
	
	public static void clear(){
		if(context.get() != null){
			context.set(null);
		}
	}
}
