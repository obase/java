package com.github.obase.mysql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

	String name();

	String[] columns();

	IndexType type() default IndexType.NULL;

	Using using() default Using.NULL;
}
