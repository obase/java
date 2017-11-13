package com.github.obase.webc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

import com.github.obase.webc.AuthType;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface ServletController {

	String value() default "";

	String path() default "";

	org.springframework.http.HttpMethod[] method() default {}; // if empty support all methods

	AuthType auth() default AuthType.DEFAULT;

	boolean csrf() default true; // check csrf

	String category() default ""; // category of the api

	String remark() default ""; // summary to the api
}
