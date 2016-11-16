package com.github.obase.mysql.asm;

import java.util.List;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.github.obase.mysql.data.IndexAnnotation;

class IndexesAnnotationVisitor extends AnnotationVisitor {

	final List<IndexAnnotation> data;

	public IndexesAnnotationVisitor(List<IndexAnnotation> data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		return this;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		IndexAnnotation index = new IndexAnnotation();
		data.add(index);
		return new IndexAnnotationVisitor(index);
	}

}
