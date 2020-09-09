package com.hyg.spring.framework.webmvc.servlet;

import lombok.Data;

import java.util.Map;
@Data
public class MyModelAndView {
    private String viewName;  //返回到哪个页面
    private Map<String,?> model;

    public MyModelAndView() {
    }

    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public MyModelAndView(String viewName) {
        this.viewName = viewName;
    }
}
