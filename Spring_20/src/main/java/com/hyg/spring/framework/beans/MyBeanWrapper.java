package com.hyg.spring.framework.beans;

public class MyBeanWrapper {
    private Object wrapperInstance;
    private Class<?> wrapperClass;

    public MyBeanWrapper(Object instance) {
        this.wrapperInstance=instance;
        this.wrapperClass=instance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }
}
