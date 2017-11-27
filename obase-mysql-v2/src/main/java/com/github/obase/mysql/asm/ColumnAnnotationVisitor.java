package com.github.obase.mysql.asm;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.annotation.SqlType;
import com.github.obase.mysql.data.ColumnAnnotation;

class ColumnAnnotationVisitor extends AnnotationVisitor {

	final ColumnAnnotation data;

	public ColumnAnnotationVisitor(ColumnAnnotation data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public void visit(String name, Object value) {
		if ("name".equals(name)) {
			data.name = (String) value;
		} else if ("comment".equals(name)) {
			data.comment = (String) value;
		} else if ("length".equals(name)) {
			data.length = (Integer) value;
		} else if ("decimals".equals(name)) {
			data.decimals = (Integer) value;
		} else if ("key".equals(name)) {
			data.key = (Boolean) value;
		} else if ("autoIncrement".equals(name)) {
			data.autoIncrement = (Boolean) value;
		} else if ("notNull".equals(name)) {
			data.notNull = (Boolean) value;
		} else if ("unique".equals(name)) {
			data.unique = (Boolean) value;
		} else if ("defaultValue".equals(name)) {
			data.defaultValue = (String) value;
		}
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		if ("type".equals(name)) {
			data.type = SqlType.valueOf(value);
		}
	}

}
