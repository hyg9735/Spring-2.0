package com.hyg.demo.action;

import com.hyg.demo.service.IModifyService;
import com.hyg.demo.service.IQueryService;
import com.hyg.spring.framework.annotation.MyAutowired;
import com.hyg.spring.framework.annotation.MyController;
import com.hyg.spring.framework.annotation.MyRequestMapping;
import com.hyg.spring.framework.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口url
 * @author Tom
 *
 */
@MyController
@MyRequestMapping("/web")
public class MyAction {

	@MyAutowired
	IQueryService queryService;
	@MyAutowired
	IModifyService modifyService;

	@MyRequestMapping("/query.json")
	public void query(HttpServletRequest request, HttpServletResponse response,
								@MyRequestParam("name") String name){
		String result = queryService.query(name);
		out(response,result);
	}
	
	@MyRequestMapping("/add*.json")
	public void add(HttpServletRequest request,HttpServletResponse response,
			   @MyRequestParam("name") String name,@MyRequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@MyRequestMapping("/remove.json")
	public void remove(HttpServletRequest request,HttpServletResponse response,
		   @MyRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@MyRequestMapping("/edit.json")
	public void edit(HttpServletRequest request,HttpServletResponse response,
			@MyRequestParam("id") Integer id,
			@MyRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}
	
	
	
	private void out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
