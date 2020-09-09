package com.hyg.spring.framework.webmvc.servlet;

import com.hyg.spring.framework.annotation.*;
import com.hyg.spring.framework.context.MyApplicationContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tom.
 */
public class MyDispatcherServlet extends HttpServlet {

    private MyApplicationContext myApplicationContext;

    private List<MyHandlerMapping> handlerMappings=new ArrayList<MyHandlerMapping>();
    private Map<MyHandlerMapping,MyHandlerAdapter>handlerAdapters=new HashMap<MyHandlerMapping, MyHandlerAdapter>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //6、委派,根据URL去找到一个对应的Method并通过response返回
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

//        if(!this.handlerMapping.containsKey(url)){
//            resp.getWriter().write("404 Not Found!!!");
//            return;
//        }
        //1.通过一个url获得一个handlerMapping
        MyHandlerMapping handlerMapping=getHandler(req);
        if (handlerMapping==null){
            processDispatchResult(req,resp,new MyModelAndView("404"));
            return;
        }
        //2.通过一个HandlerMapping获得一个HandlerAdapter
        MyHandlerAdapter handlerAdapter= getHandLerAdapter(handlerMapping);

        //3.解析某一个方法的形参和返回值之后，统一封装为ModelAndView对象
        MyModelAndView mv=handlerAdapter.handler(req,resp,handlerMapping);

        //4.把ModelAndView变成一个ViewResolver
        processDispatchResult(req,resp,mv);



    }

    private MyHandlerAdapter getHandLerAdapter(MyHandlerMapping handlerMapping) {
        if (this.handlerAdapters.isEmpty()){
            return null;
        }
        return this.handlerAdapters.get(handlerMapping);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyModelAndView myModelAndView) {

    }

    private MyHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()){return null;}
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        for (MyHandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(url);
            if (!matcher.matches()){continue;}
                return mapping;
            }
        return null;
    }

    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
//        if(chars[0] > )
        chars[0] += 32;
        return String.valueOf(chars);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化Spring核心的Ioc容器
        myApplicationContext=new MyApplicationContext(config.getInitParameter("contextConfigLocation"));

        //初始化九大组件
        initStartegies(myApplicationContext);

        System.out.println("GP Spring framework is init.");
    }

    private void initStartegies(MyApplicationContext context) {
//        //多文件上传的组件
//        initMultipartResolver(context);
//        //初始化本地语言环境
//        initLocaleResolver(context);
//        //初始化模板处理器
//        initThemeResolver(context);
        //handlerMapping
        initHandlerMappings(context);
        //初始化参数适配器
        initHandlerAdapters(context);
//        //初始化异常拦截器
//        initHandlerExceptionResolvers(context);
//        //初始化视图预处理器
//        initRequestToViewNameTranslator(context);
        //初始化视图转换器
        initViewResolvers(context);
//        //FlashMap管理器
//        initFlashMapManager(context);

    }

    private void initViewResolvers(MyApplicationContext context) {

    }

    private void initHandlerAdapters(MyApplicationContext context) {

        for (MyHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new MyHandlerAdapter());
        }
    }

    private void initHandlerMappings(MyApplicationContext context) {
    }

    private void doInitHandlerMapping() {
        if(this.myApplicationContext.getBeanDefinitionCount()==0){ return;}

        for (String beanName : this.myApplicationContext.getBeanDrginitionNames()) {
            Object instance=myApplicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();

            if(!clazz.isAnnotationPresent(MyController.class)){ continue; }


            //相当于提取 class上配置的url
            String baseUrl = "";
            if(clazz.isAnnotationPresent(MyRequestMapping.class)){
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //只获取public的方法
            for (Method method : clazz.getMethods()) {
                if(!method.isAnnotationPresent(MyRequestMapping.class)){continue;}
                //提取每个方法上面配置的url
                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);

                // //demo//query
                String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                Pattern pattern=Pattern.compile(regex);
                handlerMappings.add(new MyHandlerMapping(pattern,method,instance));
                System.out.println("Mapped : " + regex + "," + method);
            }

        }
    }
}
