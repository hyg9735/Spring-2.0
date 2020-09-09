package com.hyg.demo.action;

import com.hyg.demo.service.IQueryService;
import com.hyg.spring.framework.annotation.MyAutowired;
import com.hyg.spring.framework.annotation.MyController;
import com.hyg.spring.framework.annotation.MyRequestMapping;
import com.hyg.spring.framework.annotation.MyRequestParam;
import com.hyg.spring.framework.webmvc.servlet.MyModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@MyController
@MyRequestMapping("/")
public class PageAction {

    @MyAutowired
    IQueryService queryService;

    @MyRequestMapping("/first.html")
    public MyModelAndView query(@MyRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new MyModelAndView("first.html",model);
    }

}
