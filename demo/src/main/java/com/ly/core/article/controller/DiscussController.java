package com.ly.core.article.controller;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.ly.common.mvc.BaseController;
import com.ly.common.mvc.Box;
import com.ly.core.article.service.DiscussService;
import com.ly.core.article.vo.Discuss;




/**
 * Description: this is Discuss Controller
 * @author mtwu
 * @version 1.0 2011
 */
@Controller
@RequestMapping("/discuss")
public class DiscussController extends BaseController {
	Logger logger = LoggerFactory.getLogger(DiscussController.class);
	
	@Autowired
	private DiscussService discussService;
	
	/**
	 * , produces = "application/json;charset=utf-8"
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/list/json", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("DiscussController.list()");
	    Box box = loadNewBox(request);
	    PageInfo<Discuss> pageInfo = discussService.findPageList(box.getPagination());
	    return JSON.toJSONString(pageInfo);
	}
	
	
}

