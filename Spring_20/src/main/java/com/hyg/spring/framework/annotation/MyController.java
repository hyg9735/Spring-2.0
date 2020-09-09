package com.hyg.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * 页面交互
 * @author Tom
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
	String value() default "";
}
