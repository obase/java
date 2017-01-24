package com.github.obase.webc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.obase.webc.AuthType;

/**
 * Auto create httpservlet instance
 */

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServletMethod {

	String value() default ""; // "$": ignore the methodName

	org.springframework.http.HttpMethod[] method() default {}; // if empty support all methods

	AuthType auth() default AuthType.DEFAULT;

	boolean csrf() default true; // check csrf

	String remark() default ""; // summary to the api

	String api() default ""; // name of the api, if not empty, it will export as api

	String category() default ""; // category of the api

}
