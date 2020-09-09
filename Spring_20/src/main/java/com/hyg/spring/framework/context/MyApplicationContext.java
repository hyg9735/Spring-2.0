package com.hyg.spring.framework.context;

import com.hyg.spring.framework.annotation.MyAutowired;
import com.hyg.spring.framework.annotation.MyController;
import com.hyg.spring.framework.annotation.MyService;
import com.hyg.spring.framework.beans.MyBeanWrapper;
import com.hyg.spring.framework.beans.config.MyBeanDefinition;
import com.hyg.spring.framework.beans.support.MyBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 委派模式 ，负责任务调度
 * 职责 完成bean的创建和DI
 */
public class MyApplicationContext {

    private MyBeanDefinitionReader reader;
    private Map<String, MyBeanDefinition> beanDefinitionMap=new HashMap<String, MyBeanDefinition>();
    private Map<String ,MyBeanWrapper> factoryBeanInstanceCache=new HashMap<String, MyBeanWrapper>();
    private Map<String,Object> factoryBeanObjectCache=new HashMap<String, Object>();

    public MyApplicationContext(String... configlocations){
        try {
            //加载配置文件
            reader=new MyBeanDefinitionReader(configlocations);
            //解析配置文件 封装成beanDefinition
            List<MyBeanDefinition> beanDefinitions= reader.loadBeanDefinitions();
            //将beanDefinition缓存
            doRegistBeanDefinition(beanDefinitions);
            //依赖注入
            doAutowrited();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowrited() {
        for (Map.Entry<String, MyBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName=beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }

    private void doRegistBeanDefinition(List<MyBeanDefinition> beanDefinitions) throws Exception {
        for (MyBeanDefinition beanDefinition:beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The"+beanDefinition.getFactoryBeanName()+"is exists");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
        }
    }

    //Bean的实例化 和DI
    public Object getBean(String beanName){
        //拿到BeanDefinition
        MyBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        //反射实例化
        Object instance=instantiateBean(beanName,beanDefinition);
        //包装
        MyBeanWrapper beanWrapper=new MyBeanWrapper(instance);
        //保存到Ioc
        factoryBeanInstanceCache.put(beanName,beanWrapper);
        //执行依赖注入 beanWrapper 注入谁
        populateBean(beanName,beanDefinition,beanWrapper);
        return beanWrapper.getWrapperInstance();
    }

    private void populateBean(String beanName, MyBeanDefinition beanDefinition, MyBeanWrapper beanWrapper) {

        Object instance=beanWrapper.getWrapperInstance();
        Class clazz=beanWrapper.getWrapperClass();
        if (!(clazz.isAnnotationPresent(MyController.class)||clazz.isAnnotationPresent(MyService.class))) {return;}
            //把所有的包括private/protected/default/public 修饰字段都取出来
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(MyAutowired.class)) {
                    continue;
                }
                MyAutowired autowired = field.getAnnotation(MyAutowired.class);
                //如果用户没有自定义的beanName，就默认根据类型注入
                String autowiredBeanName = autowired.value().trim();
                if ("".equals(autowiredBeanName)) {
                    //field.getType().getName() 获取字段的类型
                    autowiredBeanName = field.getType().getName();
                }
                //暴力访问
                field.setAccessible(true);
                try {
                    if (this.factoryBeanObjectCache.get(autowiredBeanName)==null){continue;}
                    //ioc.get(beanName) 相当于通过接口的全名拿到接口的实现的实例
                    field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
    }

    private Object instantiateBean(String beanName, MyBeanDefinition beanDefinition) {
         String className=beanDefinition.getBeanClassName();
        Object instance=null;
        try {
            if (this.factoryBeanObjectCache.containsKey(beanName)) {
                instance = this.factoryBeanObjectCache.get(beanName);
            } else {
                Class<?> beanClass = Class.forName(className);
                instance = beanClass.newInstance();
                this.factoryBeanObjectCache.put(beanName, instance);
            }
            } catch(Exception e){
                e.printStackTrace();
            }
        return instance;
    }

    public Object getBean(Class beanClass){
        return getBean(beanClass.getName());
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public String[] getBeanDrginitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }
}
