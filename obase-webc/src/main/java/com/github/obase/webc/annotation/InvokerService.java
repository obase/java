package com.github.obase.webc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auto create HttpInvokerServiceExporter instance
 */

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InvokerService {

	Class<?> value();

	String summary() default ""; // summary to the service

	String category() default ""; // category of the service
}
