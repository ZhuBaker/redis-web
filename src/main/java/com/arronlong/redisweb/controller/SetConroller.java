package com.arronlong.redisweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arronlong.redisweb.common.response.WorkcenterResponseBodyJson;
import com.arronlong.redisweb.common.util.Constant;
import com.arronlong.redisweb.common.util.RedisApplication;
import com.arronlong.redisweb.service.SetService;

@Controller
@RequestMapping("/set")
public class SetConroller extends RedisApplication implements Constant {
	
	@Autowired
	private SetService setService;
	
	@RequestMapping(value="/delSetValue", method=RequestMethod.POST)
	@ResponseBody
	public Object delSetValue(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String serverName, 
			@RequestParam int dbIndex,
			@RequestParam String key,
			@RequestParam String dataType,
			@RequestParam String value) {
		
		setService.delSetValue(serverName, dbIndex, key, value);
		
		return WorkcenterResponseBodyJson.custom().build();
	}
	
	@RequestMapping(value="/updateSetValue", method=RequestMethod.POST)
	@ResponseBody
	public Object updateSetValue(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String serverName, 
			@RequestParam int dbIndex,
			@RequestParam String key,
			@RequestParam String dataType,
			@RequestParam String value) {
		
		setService.updateSetValue(serverName, dbIndex, key, value);
		
		return WorkcenterResponseBodyJson.custom().build();
	}
	
}
