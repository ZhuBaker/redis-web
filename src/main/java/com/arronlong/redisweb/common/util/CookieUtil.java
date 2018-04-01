package com.arronlong.redisweb.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;


public class CookieUtil {

	/**
	 * 默认Cookie 超时时间30分钟
	 */
	public final static Integer DEFAULT_COOKIE_TIME = 1000 * 60 * 30;
	

	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
		String ctx = request.getContextPath();
		setCookie(response,name,value,StringUtils.isEmpty(ctx) ? "/" : ctx, null ,DEFAULT_COOKIE_TIME);
	}
	
	public static void setCookie(HttpServletResponse response, String name,
			String value, String path, String domain, int maxAge) {
		
		Cookie cookie = new Cookie(name, value);
		cookie.setSecure(false);
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		if(domain != null){
			cookie.setDomain(domain);
		}
		response.addCookie(cookie);
	}
	
	public static String getMainDomain(String url){
		String cookieDomain = null;
		Pattern p = Pattern.compile("://([^:/]+)");
		Matcher m = p.matcher(url);
		String allDomin = null;
		if(m.find() && m.groupCount() > 0){
			allDomin = m.group(1);
		}
		if(allDomin != null){
			//如果是本地IP类型
			if(Pattern.matches("(\\d{1,3}\\.){3}\\d{1,3}", allDomin)){
				cookieDomain = allDomin;
			}else if(allDomin.indexOf(".") != -1){
				String patternString1 = "aero|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel";
		        String patternString2 = "ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|er|es|et|eu|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw";
		        String pattern="[\\w-]+(\\.("+patternString1+")(\\.("+patternString2+"))|\\.("+patternString1+"|"+patternString2+"))$";
		        Pattern p1 = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
		        Matcher matcher = p1.matcher(allDomin);
		        if(matcher.find()){
		        	cookieDomain = "." + matcher.group() ;
		        }
			}else{
				cookieDomain = allDomin;
			}
		}
		return cookieDomain;
	}

	
	
	/**
	 * Convenience method to get a cookie by name
	 * 
	 * @param request
	 *            the current request
	 * @param name
	 *            the name of the cookie to find
	 * 
	 * @return the cookie (if found), null if not found
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie thisCookie = cookies[i];
			if (thisCookie.getName().equals(name)) {
				if (thisCookie.getValue()!=null && !thisCookie.getValue().equals("")) {
					return thisCookie;
				}
			}
		}
		return null;
	}
	
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		if(cookie != null){
			return cookie.getValue();
		}
		return null;
	}

	/**
	 * Convenience method for deleting a cookie by name
	 * 
	 * @param response
	 *            the current web response
	 * @param cookie
	 *            the cookie to delete
	 * 
	 * @return the modified response
	 */
	public static void deleteCookie(HttpServletResponse response,
			Cookie cookie, String path) {
		if (cookie != null) {
			cookie.setMaxAge(0);
			cookie.setValue(null);
			cookie.setPath(path);
			response.addCookie(cookie);
		}
	}

	/**
	 * 清除所有的路径为"/"的Cookies
	 * 
	 * @param request
	 * @param response
	 */
	public static void deleteAllCookies(HttpServletRequest request,
			HttpServletResponse response) {
		String ctx = request.getContextPath();
		Cookie[] cooks = request.getCookies();
		for (Cookie ck : cooks){
			deleteCookie(response, ck, StringUtils.isEmpty(ctx) ? "/" : ctx);
		}
	}
	
	/**
	 * 清除所有的路径为"/"的Cookies
	 * 
	 * @param request
	 * @param response
	 */
	public static void deleteAllCookies(HttpServletRequest request,
			HttpServletResponse response, String path) {
		Cookie[] cooks = request.getCookies();
		for (Cookie ck : cooks){
			deleteCookie(response, ck, path);
		}
	}
	
	/**
	 *  对cookie进行加密
	 * @param value
	 * @return
	 */
	public static String encryptCookie(String value){
		return value;
	}
	
	/**
	 *  对cookie进行解密
	 * @param value
	 * @return
	 */
	public static String decryptCookie(String value){
		return value;
	}
	
	public static void main(String[] args) {
		System.out.println(getMainDomain("http://y.v1.cn/news/home/index.jhtml"));
		System.out.println(getMainDomain("http://127.0.0.1:8080/aaaa.do"));
		System.out.println(getMainDomain("https://aa.member.ccbd.cn:8080/aaaa.do"));
		System.out.println(getMainDomain("https://aa.member.ccbd.com.cn:8080/aaaa.do"));
	}

}
