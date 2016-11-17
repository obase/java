package com.github.obase.mysql.asm;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.annotation.Engine;
import com.github.obase.mysql.data.TableAnnotation;

class TableAnnotationVisitor extends AnnotationVisitor {

	final TableAnnotation data;

	public TableAnnotationVisitor(TableAnnotation data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public void visit(String name, Object value) {
		if ("name".equals(name)) {
			data.name = (String) value;
		} else if ("comment".equals(name)) {
			data.comment = (String) value;
		} else if ("characterSet".equals(name)) {
			data.characterSet = (String) value;
		} else if ("collate".equals(name)) {
			data.collate = (String) value;
		}
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		if ("engine".equals(name)) {
			data.engine = Engine.valueOf(value);
		}
	}

}
