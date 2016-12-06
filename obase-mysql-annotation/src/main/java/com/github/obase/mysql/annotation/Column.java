package com.github.obase.mysql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Column {

	String name() default ""; // Using field name as default;

	String comment() default "";

	SqlType type() default SqlType.NULL;

	int length() default 0;

	int decimals() default 0;

	boolean key() default false;

	boolean autoIncrement() default false; // require primaryKey > 0

	boolean notNull() default false;

	boolean unique() default false;

	String defaultValue() default "\0"; // default value as string. "": empty string; "\0": null
}
