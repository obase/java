package com.github.obase.webc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

/**
 * Auto create HttpInvokerServiceExporter instance
 */

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface InvokerService {

	String value() default "";

	Class<?> target(); // target interface to invoke

	String remark() default ""; // summary to the service

	String category() default ""; // category of the service
}
