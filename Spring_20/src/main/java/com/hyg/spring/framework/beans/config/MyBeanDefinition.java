package com.hyg.spring.framework.beans.config;

public class MyBeanDefinition {
    //在工厂里对应的名字
    private String factoryBeanName;
    //对应的全类名
    private String beanClassName;

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
