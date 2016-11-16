package com.github.obase.mysql.asm;

import java.util.LinkedList;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.annotation.Using;
import com.github.obase.mysql.data.PrimaryKeyAnnotation;

class PrimaryKeyAnnotationVisitor extends AnnotationVisitor {

	final PrimaryKeyAnnotation data;

	public PrimaryKeyAnnotationVisitor(PrimaryKeyAnnotation data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		if ("using".equals(name)) {
			data.using = Using.valueOf(value);
		}
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		if ("columns".equals(name)) {
			data.columns = new LinkedList<String>();
			return new SimpleArrayAnnotationVisitor<String>(data.columns);
		}
		return null;
	}

}
