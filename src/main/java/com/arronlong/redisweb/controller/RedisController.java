package com.arronlong.redisweb.controller;

import java.util.Map.Entry;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.arronlong.redisweb.common.WorkcenterResult;
import com.arronlong.redisweb.common.constants.Constrants;
import com.arronlong.redisweb.common.redis.RedisSerializerSetting;
import com.arronlong.redisweb.common.response.WorkcenterResponseBodyJson;
import com.arronlong.redisweb.common.util.Constant;
import com.arronlong.redisweb.common.util.ConvertUtil;
import com.arronlong.redisweb.common.util.CookieUtil;
import com.arronlong.redisweb.common.util.InitContext;
import com.arronlong.redisweb.common.util.Pagination;
import com.arronlong.redisweb.common.util.QueryEnum;
import com.arronlong.redisweb.common.util.RKey;
import com.arronlong.redisweb.common.util.RedisApplication;
import com.arronlong.redisweb.common.util.StringUtil;
import com.arronlong.redisweb.common.webcontext.WebRuntimeContextHolder;
import com.arronlong.redisweb.common.ztree.ZNode;
import com.arronlong.redisweb.service.RedisService;
import com.arronlong.redisweb.service.ViewService;

/**
 * Redis操作
 * 
 * @author cuichenglong@motie.com  
 * @date 2017年11月25日 下午5:34:43
 */
@Controller
@RequestMapping("/redis")
public class RedisController extends RedisApplication implements Constant {

	@Autowired
	private ViewService viewService;
	@Autowired
	private RedisService redisService;

	@Autowired
	private InitContext context;

	@RequestMapping(method = RequestMethod.GET)
	public Object home(HttpServletRequest request, HttpServletResponse response) {
		String defaultServerName = (String) (RedisApplication.redisServerCache.size() == 0 ? DEFAULT_REDISSERVERNAME : RedisApplication.redisServerCache.get(0).get("name"));
		request.setAttribute("serverName", defaultServerName);
		request.setAttribute("dbIndex", DEFAULT_DBINDEX);
		try {
			defaultServerName = URLEncoder.encode(defaultServerName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "redirect:/redis/stringList/" + defaultServerName + "/" + DEFAULT_DBINDEX;
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public Object index(HttpServletRequest request, HttpServletResponse response) {

		request.setAttribute("basePath", BASE_PATH);
		request.setAttribute("viewPage", "home.jsp");

		String defaultServerName = (String) (RedisApplication.redisServerCache.size() == 0 ? DEFAULT_REDISSERVERNAME : RedisApplication.redisServerCache.get(0).get("name"));
		request.setAttribute("serverName", defaultServerName);
		request.setAttribute("dbIndex", DEFAULT_DBINDEX);
		
		return "admin/main";
	}

	@RequestMapping(value = "/page/{url}", method = RequestMethod.GET)
	public Object stringList(@PathVariable String url) {
		return url;
	}

	@RequestMapping(value = "/addServer", method = RequestMethod.POST)
	@ResponseBody
	public Object addServer(HttpServletRequest request, HttpServletResponse response, @RequestParam String host,
			@RequestParam String name, @RequestParam int port, @RequestParam String password) {

		Cookie cookie = CookieUtil.getCookie(request, Constrants.REDIS_SERIALIZER);
		if (cookie != null) {
			RedisSerializerSetting settings = JSONArray.parseObject(cookie.getValue(), RedisSerializerSetting.class);
			WebRuntimeContextHolder.getRuntimeContext().setRedisSerializerSetting(settings);
		}

		redisService.addRedisServer(name, host, port, password);

		return WorkcenterResponseBodyJson.custom().build();
	}
	
	@RequestMapping(value = "/reloadServer", method = RequestMethod.GET)
	@ResponseBody
	public Object refreshServer(HttpServletRequest request, HttpServletResponse response) {
		// 重新初始化连接池
		context.reloadRedisServers();
		
		return WorkcenterResponseBodyJson.custom().build();
	}

	@RequestMapping(value = "/setRedisSerializer", method = RequestMethod.POST)
	@ResponseBody
	public Object setRedisSerializer(RedisSerializerSetting settings, HttpServletRequest request, HttpServletResponse response) {
		WebRuntimeContextHolder.getRuntimeContext().setRedisSerializerSetting(settings);
		// 重新初始化连接池
		context.reInitRedisServers();
		// 不设置Max-age，表示是session级别，关闭浏览器时自动失效。
		// Max-age=负数，则表示过期无效
		// Max-age=整数，则到期才会失效，在有效期间内，可以直接复用该Cookie
		response.addHeader("Set-Cookie", Constrants.REDIS_SERIALIZER + "=" + Base64.getEncoder().encodeToString(JSONArray.toJSONString(settings).getBytes()) + "; Max-age=2592000; Path=/;");
		return WorkcenterResponseBodyJson.custom().build();
	}

	@RequestMapping(value = "/serverTree", method = RequestMethod.GET)
	@ResponseBody
	public Object serverTree(HttpServletRequest request, HttpServletResponse response) {

		Set<ZNode> keysSet = viewService.getLeftTree();

		return keysSet;
	}

	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	@ResponseBody
	public Object refresh(HttpServletRequest request, HttpServletResponse response) {
		logCurrentTime("viewService.refresh(); start");
		viewService.refresh();
		logCurrentTime("viewService.refresh(); end");
		return WorkcenterResponseBodyJson.custom().build();
	}

	private void refreshByMode() {
		switch (refreshMode) {
		case manually:
			break;
		case auto:
			viewService.refresh();
			break;
		}
	}

	@RequestMapping(value = "/refreshMode", method = RequestMethod.POST)
	@ResponseBody
	public Object refreshMode(HttpServletRequest request, HttpServletResponse response, @RequestParam String mode) {

		viewService.changeRefreshMode(mode);

		return WorkcenterResponseBodyJson.custom().build();
	}

	@RequestMapping(value = "/changeShowType", method = RequestMethod.POST)
	@ResponseBody
	public Object changeShowType(HttpServletRequest request, HttpServletResponse response, @RequestParam String state) {

		viewService.changeShowType(state);
		return WorkcenterResponseBodyJson.custom().build();
	}

	@RequestMapping(value = "/stringList/{serverName}/{dbIndex}", method = RequestMethod.GET)
	public Object stringList(HttpServletRequest request, HttpServletResponse response, @PathVariable String serverName, @PathVariable String dbIndex) {

		refreshByMode();
		
		checkCookie(request, response);

		String queryKey = StringUtil.getParameterByDefault(request, "queryKey", MIDDLE_KEY);
		String queryKey_ch = QueryEnum.valueOf(queryKey).getQueryKeyCh();
		String queryValue = StringUtil.getParameterByDefault(request, "queryValue", EMPTY_STRING);
		String queryByKeyPrefixs = StringUtil.getParameterByDefault(request, "queryByKeyPrefixs", EMPTY_STRING);

		String[] keyPrefixs = request.getParameterValues("keyPrefixs");

		Pagination pagination = stringListPagination(request, queryKey, queryKey_ch, queryValue, queryByKeyPrefixs);

		logCurrentTime("viewService.getRedisKeys start");
		Set<RKey> redisKeys = viewService.getRedisKeys(pagination, serverName, dbIndex, keyPrefixs, queryKey, queryValue);
		logCurrentTime("viewService.getRedisKeys end");

		java.util.Map<String, String> map = new java.util.HashMap<>();
		for (Entry<String, Class<RedisSerializer<?>>> entry : Constant.redisSerializerMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue().getName());
		}

		request.setAttribute("redisServers", redisServerCache);
		request.setAttribute("redisSerializers", map);
		request.setAttribute("basePath", BASE_PATH);
		request.setAttribute("queryLabel_ch", queryKey_ch);
		request.setAttribute("queryLabel_en", queryKey);
		request.setAttribute("queryValue", queryValue);
		request.setAttribute("serverName", serverName);
		request.setAttribute("dbIndex", dbIndex);
		request.setAttribute("redisKeys", redisKeys);
		request.setAttribute("refreshMode", refreshMode.getLabel());
		request.setAttribute("change2ShowType", showType.getChange2());
		request.setAttribute("showType", showType.getState());
		request.setAttribute("pagination", pagination.createLinkTo());
		request.setAttribute("viewPage", "redis/list.jsp");
		return "admin/main";
	}

	private Pagination stringListPagination(HttpServletRequest request, String queryKey, String queryKey_ch, String queryValue, String queryByKeyPrefixs) {
		Pagination pagination = getPagination(request);
		String url = "?" + "queryKey=" + queryKey + "&queryKey_ch=" + queryKey_ch + "&queryValue=" + queryValue;
		pagination.setLink_to(url);
		if (!StringUtil.isEmpty(queryByKeyPrefixs)) {

		}
		return pagination;
	}

	private Pagination getPagination(HttpServletRequest request) {
		String items_per_page = StringUtil.getParameterByDefault(request, "items_per_page", DEFAULT_ITEMS_PER_PAGE + "");
		String num_display_entries = StringUtil.getParameterByDefault(request, "num_display_entries", "3");
		String visit_page = StringUtil.getParameterByDefault(request, "visit_page", "0");
		String num_edge_entries = StringUtil.getParameterByDefault(request, "num_edge_entries", "2");
		String prev_text = StringUtil.getParameterByDefault(request, "prev_text", "Prev");
		String next_text = StringUtil.getParameterByDefault(request, "next_text", "Next");
		String ellipse_text = StringUtil.getParameterByDefault(request, "ellipse_text", "Next");
		String prev_show_always = StringUtil.getParameterByDefault(request, "prev_show_always", "true");
		String next_show_always = StringUtil.getParameterByDefault(request, "next_show_always", "true");

		Pagination pagination = new Pagination();
		pagination.setItems_per_page(Integer.parseInt(items_per_page));
		pagination.setNum_display_entries(Integer.parseInt(num_display_entries));
		pagination.setCurrent_page(Integer.parseInt(visit_page));
		pagination.setNum_edge_entries(Integer.parseInt(num_edge_entries));
		pagination.setPrev_text(prev_text);
		pagination.setNext_text(next_text);
		pagination.setEllipse_text(ellipse_text);
		pagination.setPrev_show_always(Boolean.parseBoolean(prev_show_always));
		pagination.setNext_show_always(Boolean.parseBoolean(next_show_always));

		return pagination;
	}

	@RequestMapping(value = "/KV", method = RequestMethod.POST)
	@ResponseBody
	public Object updateKV(HttpServletRequest request, HttpServletResponse response, @RequestParam String serverName,
			@RequestParam int dbIndex, @RequestParam String dataType, @RequestParam String key) {

		String[] value = request.getParameterValues("value");
		double[] score = ConvertUtil.convert2Double(request.getParameterValues("score"));
		String[] member = request.getParameterValues("member");
		String[] field = request.getParameterValues("field");

		redisService.addKV(serverName, dbIndex, dataType, key, value, score, member, field);

		return WorkcenterResponseBodyJson.custom().build();
	}

	@RequestMapping(value = "/KV", method = RequestMethod.GET)
	@ResponseBody
	public Object getKV(HttpServletRequest request, HttpServletResponse response, @RequestParam String serverName,
			@RequestParam int dbIndex, @RequestParam String dataType, @RequestParam String key) {

		WorkcenterResult result = (WorkcenterResult) redisService.getKV(serverName, dbIndex, dataType, key);

		return WorkcenterResponseBodyJson.custom().setAll(result, GETKV).build();
	}

	@RequestMapping(value = "/delKV", method = RequestMethod.POST)
	@ResponseBody
	public Object delKV(HttpServletRequest request, HttpServletResponse response, @RequestParam String serverName,
			@RequestParam int dbIndex, @RequestParam String deleteKeys) {

		redisService.delKV(serverName, dbIndex, deleteKeys);

		return WorkcenterResponseBodyJson.custom().build();

	}
	
	/**
	 * 检查是否刚打开页面
	 * 如果刚打开，则判断cookie中是否有redis的序列化器
	 * 如果有，则使用序列化器重新初始化redisTemplate
	 * 
	 * @param request
	 * @param response
	 */
	private void checkCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = CookieUtil.getCookie(request, Constrants.SESSION_ID);
		if (cookie == null) {
			cookie = CookieUtil.getCookie(request, Constrants.REDIS_SERIALIZER);
			if (cookie != null) {
				try {
					RedisSerializerSetting settings = JSONArray.parseObject(cookie.getValue(), RedisSerializerSetting.class);
					WebRuntimeContextHolder.getRuntimeContext().setRedisSerializerSetting(settings);
					// 重新初始化连接池
					context.reInitRedisServers();
				} catch (Exception e) {}
			}
		}
	}
}
