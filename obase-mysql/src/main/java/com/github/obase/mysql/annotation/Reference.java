package com.github.obase.mysql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {

	String name();

	String[] columns();

	String targetTable();

	String[] targetColumns();

	Match match() default Match.NULL;

	Option onDelete() default Option.NULL;

	Option onUpdate() default Option.NULL;

}
