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

	String value() default ""; // "$": join methodName to lookupPath, "": ignore, other: join value to lookupPath

	org.springframework.http.HttpMethod[] method() default {}; // if empty support all methods

	AuthType auth() default AuthType.DEFAULT;

	boolean csrf() default true; // check csrf

	boolean export() default false; // export the servlet method to api access

	String summary() default ""; // summary to the servlet method

	String category() default ""; // category of the servlet method

}
