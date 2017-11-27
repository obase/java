package com.github.obase.mysql.asm;

import java.util.List;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

class SimpleArrayAnnotationVisitor<T> extends AnnotationVisitor {

	final List<T> data;

	public SimpleArrayAnnotationVisitor(List<T> data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(String name, Object value) {
		data.add((T) value);
	}

}
