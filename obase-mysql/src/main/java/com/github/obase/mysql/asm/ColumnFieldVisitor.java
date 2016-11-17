package com.github.obase.mysql.asm;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.data.ColumnAnnotation;

class ColumnFieldVisitor extends FieldVisitor {

	final ColumnAnnotation data;

	public ColumnFieldVisitor(ColumnAnnotation data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return new ColumnAnnotationVisitor(data);
	}

}
