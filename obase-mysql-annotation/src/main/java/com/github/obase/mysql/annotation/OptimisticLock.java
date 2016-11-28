package com.github.obase.mysql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The optimistic lock field should be number type, and would auto increment
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OptimisticLock {
	String column();
}
