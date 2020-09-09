package com.hyg.spring.framework.beans.support;

import com.hyg.spring.framework.beans.config.MyBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MyBeanDefinitionReader {

    //保存扫描的结果 需要注册的bean
    private List<String> regitryBeanClasses=new ArrayList<String>();
    private Properties contextConfig =new Properties();

    public MyBeanDefinitionReader(String... beanDefinitions) {
        //读文件
        doLoadConfig(beanDefinitions[0]);
        //扫描配置文件中的相关类
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    public List<MyBeanDefinition> loadBeanDefinitions() {
        //将扫描到的 封装成MyBeanDefinition
        List<MyBeanDefinition> result=new ArrayList<MyBeanDefinition>();
        try {
            for (String className:regitryBeanClasses) {
                Class<?> beanClass = Class.forName(className);

                 //保存对应的ClassName的全类名
                //beanName 1.默认类名首字母小写 2. 自定义 3.接口注入
                String beanName=toLowerFirstCase(beanClass.getSimpleName());
                String beanClassName=beanClass.getName();
                result.add(doCreateBeanDefinition(beanName,beanClassName));
                //接口
                for (Class i:beanClass.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
    }
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
        return result;
    }

    private MyBeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {
        MyBeanDefinition beanDefinition=new MyBeanDefinition();
        beanDefinition.setFactoryBeanName(beanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:",""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void doScanner(String scanPackage) {
        //jar 、 war 、zip 、rar
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());

        //当成是一个ClassPath文件夹
        for (File file : classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else {
                if(!file.getName().endsWith(".class")){continue;}
                //全类名 = 包名.类名
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                //Class.forName(className);
                regitryBeanClasses.add(className);
            }
        }
    }
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
//        if(chars[0] > )
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
