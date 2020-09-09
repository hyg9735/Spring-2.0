package com.hyg.spring.framework.webmvc.servlet;

import com.hyg.spring.framework.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyHandlerAdapter {


    public MyModelAndView handler(HttpServletRequest req, HttpServletResponse resp, MyHandlerMapping handlerMapping) {
        //保存形参列表
        //将参数名称和参数的位置的关系保存起来
        Map<String,Integer> paramValues=new HashMap<String, Integer>();
        //通过运行时的状态去拿到你
        Annotation[] [] pa =handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length ; i ++) {
            for(Annotation a : pa[i]){
                if(a instanceof MyRequestParam){
                    String paramName = ((MyRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
//                        String value = Arrays.toString(params.get(paramName))
//                                .replaceAll("\\[|\\]","")
//                                .replaceAll("\\s+",",");
//                        paramValues[i] = value;
                        paramValues.put(paramName,i);
                    }
                }
            }
        }
        //获取形参列表的所有类型
        Class<?>[] paramTypes=handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i <paramTypes.length ; i++) {
            Class<?> paramType=paramTypes[i];
            if(paramType == HttpServletRequest.class||paramType == HttpServletResponse.class){
                paramValues.put(paramType.getName(),i);
            }
        }
        //拼接实参

        Map<String, String[]> params = req.getParameterMap();
        Object[] paramValuess=new Object[paramTypes.length];

        for (Map.Entry<String, String[]> stringEntry : params.entrySet()) {
            String value=Arrays.toString(params.get(stringEntry.getKey()))
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");
            if (!paramValues.containsKey(stringEntry.getKey())){continue;}
            int index=paramValues.get(stringEntry.getKey());
            //自定义类型转换器
            paramValuess[index]=castStringValue(value,paramTypes[index]);
        }



        //暂时硬编码
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        //赋值实参列表
        method.invoke(myApplicationContext.getBean(beanName),paramValues);

        return null;
    }

    private Object castStringValue(String value, Class<?> paramType) {



    }
}
