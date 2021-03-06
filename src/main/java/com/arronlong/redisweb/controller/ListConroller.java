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
import com.arronlong.redisweb.service.ListService;

@Controller
@RequestMapping("/list")
public class ListConroller extends RedisApplication implements Constant {
	
	@Autowired
	private ListService listService;
	
	@RequestMapping(value="/delListValue", method=RequestMethod.POST)
	@ResponseBody
	public Object delListValue(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String serverName, 
			@RequestParam int dbIndex,
			@RequestParam String key,
			@RequestParam String dataType) {
		
		listService.delListValue(serverName, dbIndex, key);
		
		return WorkcenterResponseBodyJson.custom().build();
	}
	
	@RequestMapping(value="/updateListValue", method=RequestMethod.POST)
	@ResponseBody
	public Object updateListValue(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String serverName, 
			@RequestParam int dbIndex,
			@RequestParam String key,
			@RequestParam String dataType,
			@RequestParam String value) {
		
		listService.updateListValue(serverName, dbIndex, key, value);
		
		return WorkcenterResponseBodyJson.custom().build();
	}
	
}
